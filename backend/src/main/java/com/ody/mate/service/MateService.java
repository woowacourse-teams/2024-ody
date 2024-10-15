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
import com.ody.notification.service.NotificationService;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MateService {

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
        validateAlreadyAttended(member, meeting);
        if (meeting.isOverdue()) {
            throw new OdyBadRequestException("참여 가능한 시간이 지난 약속에 참여할 수 없습니다.");
        }

        Mate mate = saveMateAndEta(mateSaveRequest, member, meeting);
        notificationService.saveAndSendNotifications(meeting, mate, member.getDeviceToken());
        return MateSaveResponseV2.from(meeting);
    }

    public void validateAlreadyAttended(Member member, Meeting meeting) {
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("약속에 이미 참여한 회원입니다.");
        }
    }

    private Mate saveMateAndEta(MateSaveRequestV2 mateSaveRequest, Member member, Meeting meeting) {
        RouteTime firstRouteTime = calcaulteFirstRouteTime(mateSaveRequest, meeting);
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member, firstRouteTime.getMinutes()));
        etaService.saveFirstEtaOfMate(mate, firstRouteTime);
        return mate;
    }

    private RouteTime calcaulteFirstRouteTime(MateSaveRequestV2 mateSaveRequest, Meeting meeting) {
        Coordinates originCoordinates = mateSaveRequest.toOriginCoordinates();
        Coordinates targetCoordinates = meeting.getTargetCoordinates();
        RouteTime routeTime = routeService.calculateRouteTime(originCoordinates, targetCoordinates);
        if (routeTime.isZero()) {
            return routeTime.addMinutes(10L);
        }
        return routeTime;
    }

    public List<Mate> findAllByMeetingIdIfMate(Member member, long meetingId) {
        findByMeetingIdAndMemberId(meetingId, member.getId());
        return mateRepository.findAllByOverdueFalseMeetingId(meetingId);
    }

    @Transactional
    public void nudge(NudgeRequest nudgeRequest) {
        Mate requestMate = findFetchedMate(nudgeRequest.requestMateId());
        Mate nudgedMate = findFetchedMate(nudgeRequest.nudgedMateId());

        if (requestMate.isAttended(nudgedMate.getMeeting()) && canNudge(nudgedMate)) {
            notificationService.sendNudgeMessage(requestMate, nudgedMate);
            return;
        }
        throw new OdyBadRequestException("재촉한 참여자가 같은 약속 참여자가 아니거나 지각/지각위기가 아닙니다");
    }

    private Mate findFetchedMate(Long mateId) {
        Mate mate = mateRepository.findFetchedMateById(mateId)
                .orElseThrow(() -> new OdyBadRequestException("존재하지 않는 약속 참여자입니다."));
        if (mate.getMeeting().isOverdue()) {
            throw new OdyBadRequestException("기한이 지난 약속입니다.");
        }
        return mate;
    }

    private boolean canNudge(Mate mate) {
        EtaStatus etaStatus = etaService.findEtaStatus(mate);
        return etaStatus == EtaStatus.LATE_WARNING || etaStatus == EtaStatus.LATE;
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
    public void deleteByMemberId(long memberId) {
        mateRepository.findAllByMemberId(memberId)
                .forEach(this::delete);
    }

    private void delete(Mate mate) {
        notificationService.saveMemberDeletionNotification(mate);

        notificationService.updateAllStatusPendingToDismissedByMateId(mate.getId());
        etaService.deleteByMateId(mate.getId());
        mateRepository.deleteById(mate.getId());
    }
}
