package com.ody.eta.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.dto.response.MateEtaResponse;
import com.ody.eta.dto.response.MateEtaResponses;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.DistanceCalculator;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {
    private static final long ODSAY_CALL_CYCLE_MINUTES = 10L;
    private static final int ARRIVED_THRESHOLD_METER = 300;

    private final RouteService routeService;
    private final EtaRepository etaRepository;
    private final MateRepository mateRepository;

    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }

    @Transactional
    public MateEtaResponses findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        Mate requestMate = findByMeetingIdAndMemberId(meetingId, member.getId());
        Meeting meeting = requestMate.getMeeting();
        LocalDateTime meetingTime = meeting.getMeetingTime();
        Eta mateEta = findByMateId(requestMate.getId());
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        if (determineArrived(mateEtaRequest, meeting, now)) {
            mateEta.updateArrived();
        }

        if (!mateEta.isArrived() && isOdysayCallTime(mateEta)) {
            RouteTime routeTime = routeService.calculateRouteTime(requestMate.getOrigin(), meeting.getTarget());
            mateEta.updateRemainingMinutes(routeTime.getMinutes());
        }

        List<MateEtaResponse> mateEtaResponses = etaRepository.findAllByMeetingId(meetingId).stream()
                .map(eta -> MateEtaResponse.of(eta, mateEtaRequest.isMissing(), meetingTime, now))
                .toList();
        return new MateEtaResponses(requestMate.getNicknameValue(), mateEtaResponses);
    }

    private boolean isOdysayCallTime(Eta mateEta) {
        return !mateEta.isModified() || mateEta.differenceMinutesFromLastUpdated() >= ODSAY_CALL_CYCLE_MINUTES;
    }

    private boolean determineArrived(MateEtaRequest mateEtaRequest, Meeting meeting, LocalDateTime now) {
        LocalDateTime meetingTime = meeting.getMeetingTime();
        double distance = DistanceCalculator.calculate(
                Double.valueOf(mateEtaRequest.currentLatitude()),
                Double.valueOf(mateEtaRequest.currentLongitude()),
                Double.valueOf(meeting.getTarget().getLatitude()),
                Double.valueOf(meeting.getTarget().getLongitude())
        );
        return distance <= ARRIVED_THRESHOLD_METER && (now.isBefore(meetingTime) || now.isEqual(meetingTime));
    }

    private Mate findByMeetingIdAndMemberId(Long meetingId, Long memberId) {
        return mateRepository.findByMeetingIdAndMemberId(meetingId, memberId)
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속방입니다"));
    }

    private Eta findByMateId(Long mateId) {
        return etaRepository.findByMateId(mateId)
                .orElseThrow(() -> new OdyNotFoundException("ETA 정보를 찾을 수 없습니다"));
    }
}
