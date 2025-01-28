package com.ody.meeting.service;

import com.ody.common.aop.DistributedLock;
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
import com.ody.meeting.dto.response.MeetingWithMatesResponse;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.notice.EtaNotice;
import com.ody.notification.domain.notice.NoticeType;
import com.ody.notification.service.NoticeService;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private static final long ETA_NOTICE_TIME_DEFER = 30L;
    private static final LocalTime MEETING_TIME_FOR_SCHEDULING_NOTI = LocalTime.of(5, 0);

    private final MateService mateService;
    private final MeetingRepository meetingRepository;
    private final MateRepository mateRepository;
    private final NotificationService notificationService;
    private final EtaSchedulingService etaSchedulingService;
    private final NoticeService noticeService;

    @Transactional
    public MeetingSaveResponseV1 saveV1(MeetingSaveRequestV1 meetingSaveRequestV1) {
        String inviteCode = generateUniqueInviteCode();
        Meeting meeting = meetingRepository.save(meetingSaveRequestV1.toMeeting(inviteCode));
        scheduleEtaNotice(meeting);
        scheduleNoticeIfUpcomingMeeting(meeting); // TODO: 예외상황) 약속시간 30분 이내 약속 생성의 경우, 참가한 Mate가 없어 스케줄링 사이클이 걸리지 않음. 스케줄링 없이 바로 오디창이 시작되는 경우 안드에서 처리할지, Mate save 시점에 엣지 케이스 둘지 고민.
        return MeetingSaveResponseV1.from(meeting);
    }

    private void scheduleEtaNotice(Meeting meeting) {
        LocalDateTime etaNoticeTime = meeting.getMeetingTime().minusMinutes(ETA_NOTICE_TIME_DEFER);

        EtaNotice etaNotice = new EtaNotice(etaNoticeTime, meeting);
        GroupMessage noticeMessage = GroupMessage.create(etaNotice, new FcmTopic(meeting));

        noticeService.schedule(etaNotice, noticeMessage, etaNoticeTime);
        log.info("{} 타입 알림 {}에 스케줄링 예약", NoticeType.ETA_NOTICE, etaNoticeTime);
    }

    private void scheduleNoticeIfUpcomingMeeting(Meeting meeting) {
        LocalDateTime meetingDateTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
        if (isUpcomingMeeting(meetingDateTime)) {
            etaSchedulingService.sendNotice(meeting);
            log.info("당일 약속 1건 스케줄링 알림 예약 완료");
        }
    }

    private boolean isUpcomingMeeting(LocalDateTime meetingDateTime) {
        LocalDateTime include = TimeUtil.nowWithTrim();
        LocalDateTime exclude = LocalDateTime.of(LocalDate.now().plusDays(1L), MEETING_TIME_FOR_SCHEDULING_NOTI);

        return meetingDateTime.isAfter(include) && meetingDateTime.isBefore(exclude);
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
        Mate mate = mateRepository.findByMeetingIdAndMemberId(meeting.getId(), member.getId())
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속입니다"));
        return MeetingFindByMemberResponse.of(meeting, mateCount, mate);
    }

    public MeetingWithMatesResponse findMeetingWithMates(Member member, Long meetingId) {
        Meeting meeting = findByIdAndOverdueFalse(meetingId);
        List<Mate> mates = mateService.findAllByMeetingIdIfMate(member, meeting.getId());
        return MeetingWithMatesResponse.of(meeting, mates);
    }

    @Transactional
    @DistributedLock(key = "'MATE_SAVE'")
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
    @Scheduled(cron = "0 30 4 * * *", zone = "Asia/Seoul")
    public void scheduleTodayMeetingNotices() {
        LocalDate today = LocalDate.now();
        List<Meeting> meetings = findUpcomingMeetingsWithin24Hours(today);
        meetings.forEach(etaSchedulingService::sendNotice);
        log.info("당일 ETA 스케줄링 알림 {}개 등록", meetings.size());
    }

    private List<Meeting> findUpcomingMeetingsWithin24Hours(LocalDate startDate) {
        return meetingRepository.findAllWithInDateTimeRange(
                startDate,
                MEETING_TIME_FOR_SCHEDULING_NOTI,
                startDate.plusDays(1L),
                MEETING_TIME_FOR_SCHEDULING_NOTI
        );
    }
}
