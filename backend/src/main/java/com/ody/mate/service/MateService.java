package com.ody.mate.service;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.service.EtaService;
import com.ody.mate.domain.Mate;
import com.ody.mate.dto.request.MateSaveRequest;
import com.ody.mate.dto.request.NudgeRequest;
import com.ody.mate.dto.response.MateSaveResponse;
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

    public MateSaveResponse saveAndSendNotifications(MateSaveRequest mateSaveRequest, Member member, Meeting meeting) {
        if (mateRepository.existsByMeetingIdAndNickname_Value(meeting.getId(), mateSaveRequest.nickname())) {
            throw new OdyBadRequestException("모임 내 같은 닉네임이 존재합니다.");
        }
        if (mateRepository.existsByMeetingIdAndMemberId(meeting.getId(), member.getId())) {
            throw new OdyBadRequestException("모임에 이미 참여한 회원입니다.");
        }

        Coordinates origin = new Coordinates(mateSaveRequest.originLatitude(), mateSaveRequest.originLongitude());
        RouteTime routeTime = routeService.calculateRouteTime(origin, meeting.getTargetCoordinates());
        Mate mate = mateRepository.save(mateSaveRequest.toMate(meeting, member, routeTime.getMinutes()));
        etaService.saveFirstEtaOfMate(mate, routeTime);
        notificationService.saveAndSendNotifications(meeting, mate, member.getDeviceToken(), routeTime);
        return MateSaveResponse.from(mate);
    }

    public List<Mate> findAllByMeetingIdIfMate(Member member, long meetingId) {
        findByMeetingIdAndMemberId(meetingId, member.getId());
        return mateRepository.findAllByMeetingId(meetingId);
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
        return mateRepository.findFetchedMateById(mateId)
                .orElseThrow(() -> new OdyBadRequestException("존재하지 않는 약속 참여자입니다."));
    }

    private boolean canNudge(Mate mate) {
        EtaStatus etaStatus = etaService.findEtaStatus(mate);
        return etaStatus == EtaStatus.LATE_WARNING || etaStatus == EtaStatus.LATE;
    }

    public MateEtaResponsesV2 findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        Mate mate = findByMeetingIdAndMemberId(meetingId, member.getId());
        return etaService.findAllMateEtas(mateEtaRequest, mate);
    }

    private Mate findByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        return mateRepository.findByMeetingIdAndMemberId(meetingId, memberId)
                .orElseThrow(() -> new OdyNotFoundException("존재하지 않는 약속이거나 약속 참여자가 아닙니다."));
    }
}
