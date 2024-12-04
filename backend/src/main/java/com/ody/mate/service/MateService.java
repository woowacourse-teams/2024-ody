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
import com.ody.member.domain.Member;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.types.DepartureReminder;
import com.ody.notification.domain.types.Entry;
import com.ody.notification.domain.types.MateLeave;
import com.ody.notification.domain.types.MemberDeletion;
import com.ody.notification.service.NotificationService;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

    private static final long AVAILABLE_NUDGE_DURATION = 30L;

    private final MateRepository mateRepository;
    private final EtaService etaService;
    private final NotificationService notificationService;
    private final RouteService routeService;

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
        sendEntry(meeting, mate, fcmTopic);
        notificationService.subscribeTopic(member.getDeviceToken(), fcmTopic);
        sendDepartureReminder(meeting, mate, fcmTopic);
        return MateSaveResponseV2.from(meeting);
    }

    private void sendEntry(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        Entry entry = new Entry(mate, meeting, fcmTopic);
        notificationService.saveAndSchedule(entry.toNotification());
    }

    private void sendDepartureReminder(Meeting meeting, Mate mate, FcmTopic fcmTopic) {
        DepartureTime departureTime = new DepartureTime(meeting, mate.getEstimatedMinutes());
        DepartureReminder departureReminder = new DepartureReminder(mate, departureTime, fcmTopic);
        notificationService.saveAndSchedule(departureReminder.toNotification());
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
        notificationService.sendNudgeMessage(requestMate, nudgedMate);
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

    private Mate findFetchedMate(Long mateId) {
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
        Notification memberDeletionNotification = new MemberDeletion(mate).toNotification();
        notificationService.save(memberDeletionNotification);
        delete(mate);
    }

    @Transactional
    public void leaveByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        Mate mate = findByMeetingIdAndMemberId(meetingId, memberId);
        Notification leaveNotification = new MateLeave(mate).toNotification();
        notificationService.save(leaveNotification);
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
