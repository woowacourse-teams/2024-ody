package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.service.EtaService;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequestV2;
import com.ody.mate.dto.request.NudgeRequest;
import com.ody.mate.dto.response.MateSaveResponseV2;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Coordinates;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.meetinglog.domain.MeetingLog;
import com.ody.meetinglog.domain.MeetingLogType;
import com.ody.meetinglog.service.MeetingLogService;
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.types.DepartureReminder;
import com.ody.notification.domain.types.Entry;
import com.ody.notification.domain.types.Nudge;
import com.ody.notification.service.NotificationService;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.ScheduleRunner;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private static final long AVAILABLE_NUDGE_DURATION = 30L;

    private final MateRepository mateRepository;
    private final MeetingLogService meetingLogService;
    private final EtaService etaService;
    private final NotificationService notificationService;
    private final RouteService routeService;
    private final ScheduleRunner scheduleRunner;

    @Transactional
    public MateSaveResponseV2 saveAndSendNotifications(
            MateSaveRequestV2 mateSaveRequest,
            Member member,
            Meeting meeting
    ) {
        validateMeetingOverdue(meeting);
        validateAlreadyAttended(member, meeting);
        Mate mate = saveMateAndEta(mateSaveRequest, member, meeting);

        FcmTopic fcmTopic = new FcmTopic(meeting);
        saveEntryLog(mate);
        sendEntryNotification(mate, fcmTopic);
        notificationService.subscribeTopic(member.getDeviceToken(), fcmTopic);
        sendDepartureReminder(meeting, mate, fcmTopic);
        return MateSaveResponseV2.from(meeting);
    }

    private void saveEntryLog(Mate mate) {
        MeetingLog entryLog = new MeetingLog(mate, MeetingLogType.ENTRY_LOG);
        meetingLogService.save(entryLog);
    }

    private void sendEntryNotification(Mate mate, FcmTopic fcmTopic) {
        Entry entry = new Entry(mate, fcmTopic);
        notificationService.saveAndSend(entry.toNotification());
    }

    private void sendDepartureReminder(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        DepartureTime departureTime = new DepartureTime(meeting, mate.getEstimatedMinutes());
        DepartureReminder departureReminder = new DepartureReminder(mate, departureTime, fcmTopic);
        Notification savedDepartureReminder = notificationService.save(departureReminder.toNotification());
        LocalDateTime sendAt = savedDepartureReminder.getSendAt();

        scheduleRunner.runWithTransaction(() -> {
            Mate savedMate = findFetchedMate(mate.getId());
            boolean sended = notificationService.send(savedDepartureReminder);
            if (sended) {
                MeetingLog departureReminderLog = new MeetingLog(savedMate, MeetingLogType.DEPARTURE_REMINDER);
                meetingLogService.save(departureReminderLog);
            }
        }, sendAt);
        log.info("출발 알림 알림 {}에 스케줄링 예약", sendAt);
    }

    private void validateMeetingOverdue(Meeting meeting) {
        if (meeting.isOverdue()) {
            throw new OdyBadRequestException("참여 가능한 시간이 지난 약속에 참여할 수 없습니다.");
        }
    }

    public void validateAlreadyAttended(Member member, Meeting meeting) {
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("약속에 이미 참여했습니다.");
        }
    }

    private Mate saveMateAndEta(MateSaveRequestV2 mateSaveRequest, Member member, Meeting meeting) {
        Coordinates origin = mateSaveRequest.toOriginCoordinates();
        Coordinates target = meeting.getTargetCoordinates();
        RouteTime routeTime = routeService.calculateRouteTime(origin, target);
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member, routeTime.getMinutes()));
        etaService.saveFirstEtaOfMate(mate, routeTime);
        return mate;
    }

    public List<Mate> findAllByMeetingIdIfMate(Member member, long meetingId) {
        findByMeetingIdAndMemberId(meetingId, member.getId());
        return mateRepository.findAllByOverdueFalseMeetingId(meetingId);
    }

    @Transactional
    public void nudge(NudgeRequest nudgeRequest) {
        Mate requestMate = findFetchedMate(nudgeRequest.requestMateId());
        Mate nudgedMate = findFetchedMate(nudgeRequest.nudgedMateId());
        validateNudgeCondition(requestMate, nudgedMate);

        MeetingLog nudgeLog = new MeetingLog(nudgedMate, MeetingLogType.NUDGE_LOG);
        meetingLogService.save(nudgeLog);

        Nudge nudge = new Nudge(nudgedMate);
        notificationService.sendNudgeMessage(requestMate, nudge);
    }

    private void validateNudgeCondition(Mate requestMate, Mate nudgedMate) {
        if (!requestMate.isAttended(nudgedMate.getMeeting())) {
            throw new OdyBadRequestException("재촉한 참여자가 같은 약속 참여자가 아닙니다");
        }

        if (!canNudgedStatus(nudgedMate)) {
            throw new OdyBadRequestException("재촉한 참여자가 지각/지각위기가 아닙니다");
        }

        if (!isWithinNudgeTime(nudgedMate.getMeeting())) {
            throw new OdyBadRequestException("재촉할 수 있는 시간이 지난 요청입니다");
        }
    }

    private boolean isWithinNudgeTime(Meeting meeting) {
        LocalDateTime nudgeEndTime = meeting.getMeetingTime().plusMinutes(AVAILABLE_NUDGE_DURATION);
        return !TimeUtil.nowWithTrim().isAfter(nudgeEndTime);
    }

    private boolean canNudgedStatus(Mate mate) {
        EtaStatus etaStatus = etaService.findEtaStatus(mate);
        return etaStatus == EtaStatus.LATE_WARNING || etaStatus == EtaStatus.LATE;
    }

    private Mate findFetchedMate(long mateId) {
        Mate mate = mateRepository.findFetchedMateById(mateId)
                .orElseThrow(() -> new OdyBadRequestException("존재하지 않는 약속 참여자입니다."));
        if (mate.getMeeting().isOverdue()) {
            throw new OdyBadRequestException("기한이 지난 약속입니다.");
        }
        return mate;
    }

    @Transactional
    public MateEtaResponsesV2 findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        Mate mate = findByMeetingIdAndMemberId(meetingId, member.getId());
        return etaService.findAllMateEtas(mateEtaRequest, mate);
    }

    private Mate findByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        return mateRepository.findByMeetingIdAndMemberId(meetingId, memberId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 약속이거나 약속 참여자가 아닙니다."));
    }

    @Transactional
    public void deleteAllByMember(Member member) {
        mateRepository.findFetchedAllByMemberId(member.getId())
                .forEach(this::withdraw);
    }

    @Transactional
    public void withdraw(Mate mate) {
        MeetingLog deletionLog = new MeetingLog(mate, MeetingLogType.MEMBER_DELETION_LOG);
        meetingLogService.save(deletionLog);
        delete(mate);
    }

    @Transactional
    public void leaveByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        Mate mate = findByMeetingIdAndMemberId(meetingId, memberId);
        MeetingLog leaveLog = new MeetingLog(mate, MeetingLogType.LEAVE_LOG);
        meetingLogService.save(leaveLog);
        delete(mate);
    }

    @Transactional
    public void delete(Mate mate) {
        notificationService.updateAllStatusToDismissByMateIdAndSendAtAfterNow(mate.getId());
        notificationService.unSubscribeTopic(mate.getMeeting(), mate.getMember().getDeviceToken());
        etaService.deleteByMateId(mate.getId());
        mateRepository.deleteById(mate.getId());
    }
}
