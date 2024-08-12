package com.ody.eta.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.dto.response.MateEtaResponse;
import com.ody.eta.dto.response.MateEtaResponses;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.DistanceCalculator;
import java.util.stream.Collectors;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {

    private static final long ROUTE_CLIENT_CALL_CYCLE_MINUTES = 10L;
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
        Eta mateEta = findByMateId(requestMate.getId());
        mateEta.updateMissingBy(mateEtaRequest.isMissing());

        if (determineArrived(mateEtaRequest, meeting) && !mateEta.isMissing()) {
            mateEta.updateArrived();
        }
        if ((!mateEta.isArrived() && isRouteClientCallTime(mateEta)) && !mateEta.isMissing()) {
            Location currentLocation = new Location(
                    mateEtaRequest.currentLatitude(),
                    mateEtaRequest.currentLongitude()
            );
            RouteTime routeTime = routeService.calculateRouteTime(currentLocation, meeting.getTarget());
            mateEta.updateRemainingMinutes(routeTime.getMinutes());
        }

        return etaRepository.findAllByMeetingId(meetingId).stream()
                .map(eta -> MateEtaResponse.of(eta, meeting))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        mateEtaResponses -> new MateEtaResponses(requestMate.getNicknameValue(), mateEtaResponses))
                );
    }

    private boolean isRouteClientCallTime(Eta mateEta) {
        return !mateEta.isModified() || mateEta.differenceMinutesFromLastUpdated() >= ROUTE_CLIENT_CALL_CYCLE_MINUTES;
    }

    private boolean determineArrived(MateEtaRequest mateEtaRequest, Meeting meeting) {
        double distance = DistanceCalculator.calculate(
                new Location(mateEtaRequest.currentLatitude(), mateEtaRequest.currentLongitude()), meeting.getTarget()
        );
        return distance <= ARRIVED_THRESHOLD_METER && !meeting.isEnd();
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
