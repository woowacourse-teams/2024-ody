package com.ody.meeting.service;

import com.ody.common.aop.EnableDeletedFilter;
import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.service.EtaSchedulingService;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.response.MateSaveResponseV2;
import com.ody.mate.repository.MateRepository;
import com.ody.mate.service.MateService;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.request.MeetingSaveRequestV1;
import com.ody.meeting.dto.response.MeetingFindByMemberResponse;
import com.ody.meeting.dto.response.MeetingFindByMemberResponses;
import com.ody.meeting.dto.response.MeetingSaveResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV1;
import com.ody.meeting.dto.response.MeetingWithMatesResponseV2;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.notice.EtaNotice;
import com.ody.notification.domain.notice.NoticeType;
import com.ody.notification.service.NoticeSender;
import com.ody.notification.service.NotificationService;
import com.ody.util.InviteCodeGenerator;
import com.ody.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@EnableDeletedFilter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private static final long ETA_NOTICE_TIME_DEFER = 30L;
    private static final LocalTime LAST_SCHEDULING_NOTI_TIME = LocalTime.of(5, 0);

    private final MateService mateService;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final NotificationService notificationService;
    private final EtaSchedulingService etaSchedulingService;
    private final NoticeSender noticeSender;

    @Transactional
    public MeetingSaveResponseV1 saveV1(MeetingSaveRequestV1 meetingSaveRequestV1) {
        String inviteCode = generateUniqueInviteCode();
        Meeting meeting = meetingRepository.save(meetingSaveRequestV1.toMeeting(inviteCode));
        scheduleEtaNotice(meeting);
        scheduleEtaSchedulingNoticeIfUpcomingMeeting(meeting);
        return MeetingSaveResponseV1.from(meeting);
    }

    private void scheduleEtaNotice(Meeting meeting) {
        LocalDateTime etaNoticeTime = meeting.getMeetingTime().minusMinutes(ETA_NOTICE_TIME_DEFER);

        EtaNotice etaNotice = new EtaNotice(etaNoticeTime, meeting);
        GroupMessage noticeMessage = GroupMessage.create(etaNotice, new FcmTopic(meeting));

        noticeSender.schedule(etaNotice, noticeMessage, etaNoticeTime);
        log.info("{} 타입 알림 {}에 스케줄링 예약", NoticeType.ETA_NOTICE, etaNoticeTime);
    }

    private void scheduleEtaSchedulingNoticeIfUpcomingMeeting(Meeting meeting) {
        LocalDateTime meetingDateTime = meeting.getMeetingTime();
        if (isUpcomingMeeting(meetingDateTime)) {
            etaSchedulingService.sendTrigger(meeting);
            log.info("당일 약속 1건 스케줄링 알림 예약 완료");
        }
    }

    //TODO 이거 왜 이렇게 구현했었는지 물어보기
    private boolean isUpcomingMeeting(LocalDateTime meetingDateTime) {
        LocalDateTime include = TimeUtil.nowWithTrim();
        LocalDateTime exclude = LocalDateTime.of(LocalDate.now().plusDays(1L), LAST_SCHEDULING_NOTI_TIME);

        return meetingDateTime.isAfter(include) && meetingDateTime.isBefore(exclude);
//        return meetingDateTime.isAfter(include);
    }

    private String generateUniqueInviteCode() {
        String inviteCode = InviteCodeGenerator.generate();
        while (meetingRepository.existsByInviteCode(inviteCode)) {
            inviteCode = InviteCodeGenerator.generate();
        }
        return inviteCode;
    }

    public void validateInviteCode(Member member, String inviteCode) {
        Meeting meeting = findByInviteCode(inviteCode);
        mateService.validateAlreadyAttended(member, meeting);
    }

    private Meeting findByInviteCode(String inviteCode) {
        return meetingRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 초대코드입니다."));
    }

    public Meeting findByIdAndOverdueFalse(Long meetingId) {
        return meetingRepository.findByIdAndOverdueFalse(meetingId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 약속입니다."));
    }

    public MeetingFindByMemberResponses findAllByMember(Member member) {
        return meetingRepository.findAllByMemberId(member.getId())
                .stream()
                .filter(Meeting::isWithinPast24HoursOrLater)
                .map(meeting -> makeMeetingFindByMemberResponse(member, meeting))
                .sorted(Comparator.comparing(MeetingFindByMemberResponse::date)
                        .thenComparing(MeetingFindByMemberResponse::time))
                .collect(Collectors.collectingAndThen(Collectors.toList(), MeetingFindByMemberResponses::new));
    }

    private MeetingFindByMemberResponse makeMeetingFindByMemberResponse(Member member, Meeting meeting) {
        int mateCount = mateRepository.countByMeetingId(meeting.getId());
        Mate mate = findMateByMemberAndMeeting(member, meeting);
        return MeetingFindByMemberResponse.of(meeting, mateCount, mate);
    }

    public MeetingWithMatesResponseV1 findMeetingWithMatesV1(Member member, Long meetingId) {
        Meeting meeting = findByIdAndOverdueFalse(meetingId);
        List<Mate> mates = mateService.findAllByMeetingIdIfMate(member, meeting.getId());
        return MeetingWithMatesResponseV1.of(meeting, mates);
    }

    public MeetingWithMatesResponseV2 findMeetingWithMatesV2(Member member, Long meetingId) {
        Meeting meeting = findByIdAndOverdueFalse(meetingId);
        Mate requestMate = findMateByMemberAndMeeting(member, meeting);
        List<Mate> mates = mateService.findAllByMeetingIdIfMate(member, meeting.getId());
        return MeetingWithMatesResponseV2.of(meeting, requestMate, mates);
    }

    private Mate findMateByMemberAndMeeting(Member member, Meeting meeting) {
        return mateRepository.findByMeetingIdAndMemberId(meeting.getId(), member.getId())
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속입니다"));
    }

    @Transactional
    public MateSaveResponseV2 saveMateAndSendNotifications(MateSaveRequestV2 mateSaveRequest, Member member) {
        Meeting meeting = findByInviteCode(mateSaveRequest.inviteCode());
        if (meeting.isEnd()) {
            throw new OdyBadRequestException("과거 약속에 참여할 수 없습니다.");
        }
        return mateService.saveAndSendNotifications(mateSaveRequest, member, meeting);
    }

    @Transactional
    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void scheduleOverdueMeetings() {
        meetingRepository.updateAllByNotOverdueMeetings();
        List<Meeting> meetings = meetingRepository.findAllByUpdatedTodayAndOverdue();
        log.info("약속 시간이 지난 약속들 overdue = true로 update 쿼리 실행");
        notificationService.unSubscribeTopic(meetings);
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void scheduleTodayMeetingNotices() {
        LocalDateTime today = TimeUtil.nowWithTrim();
        List<Meeting> meetings = findUpcomingMeetingsWithin24Hours(today.toLocalDate(), today.toLocalTime());
        meetings.forEach(etaSchedulingService::sendTrigger);
        log.info("당일 ETA 스케줄링 알림 {}개 등록", meetings.size());
    }

    private List<Meeting> findUpcomingMeetingsWithin24Hours(LocalDate startDate, LocalTime startTime) {
        return meetingRepository.findAllByDateTimeInClosedOpenRange(
                startDate,
                startTime,
                startDate.plusDays(1L),
                LAST_SCHEDULING_NOTI_TIME
        );
    }
}
