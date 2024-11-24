package com.ody.eta.service;

import com.ody.common.aop.DistributedLock;
import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Coordinates;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponseV2;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import com.ody.util.DistanceCalculator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {

    private static final long ROUTE_CLIENT_CALL_CYCLE_MINUTES = 10L;
    private static final int ARRIVED_THRESHOLD_METER = 300;

    private final RouteService routeService;
    private final EtaRepository etaRepository;

    @Transactional
    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }

    @Transactional
    @DistributedLock(key = "'FIND_ETAS'")
    public MateEtaResponsesV2 findAllMateEtas(MateEtaRequest mateEtaRequest, Mate mate) {
        Meeting meeting = mate.getMeeting();
        Eta mateEta = findByMateId(mate.getId());

        updateMateEta(mateEtaRequest, mateEta, meeting);

        return etaRepository.findAllByMeetingId(meeting.getId()).stream()
                .map(eta -> MateEtaResponseV2.of(eta, meeting))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        mateEtas -> new MateEtaResponsesV2(mate.getId(), mateEtas)
                ));
    }

    private void updateMateEta(MateEtaRequest mateEtaRequest, Eta mateEta, Meeting meeting) {
        mateEta.updateMissingBy(mateEtaRequest.isMissing());
        if (mateEta.isMissing()) {
            return;
        }

        if (determineArrived(mateEtaRequest.toCoordinates(), meeting)) {
            mateEta.updateArrived();
            return;
        }

        if (isRouteClientCallTime(mateEta)) {
            MDC.put("mateId", mateEta.getMate().getId().toString());
            RouteTime routeTime = routeService.calculateRouteTime(
                    mateEtaRequest.toCoordinates(),
                    meeting.getTargetCoordinates()
            );
            mateEta.updateRemainingMinutes(routeTime.getMinutes());
        }
    }

    public EtaStatus findEtaStatus(Mate mate) {
        Eta mateEta = findByMateId(mate.getId());
        return EtaStatus.of(mateEta, mate.getMeeting());
    }

    private boolean isRouteClientCallTime(Eta mateEta) {
        return !mateEta.isModified() || mateEta.differenceMinutesFromLastUpdated() >= ROUTE_CLIENT_CALL_CYCLE_MINUTES;
    }

    private boolean determineArrived(Coordinates origin, Meeting meeting) {
        double distance = DistanceCalculator.calculate(origin, meeting.getTargetCoordinates());
        return distance <= ARRIVED_THRESHOLD_METER && !meeting.isEnd();
    }

    private Eta findByMateId(Long mateId) {
        return etaRepository.findByMateId(mateId)
                .orElseThrow(() -> new OdyNotFoundException("ETA 정보를 찾을 수 없습니다"));
    }

    public void deleteByMateId(long mateId) {
        etaRepository.findByMateId(mateId)
                .ifPresent(etaRepository::delete);
    }
}
