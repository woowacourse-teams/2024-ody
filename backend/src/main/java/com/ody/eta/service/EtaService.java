package com.ody.eta.service;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EtaService {

    private final EtaRepository etaRepository;
    private final RouteService routeService;
    private final MateRepository mateRepository;

    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }

    @Transactional
    public MateEtaResponses findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        //1. 도착 여부 판단 -보류

        //2. ODSAY 발송 여부 판단
        Mate mate = mateRepository.findByMeetingIdAndMemberId(meetingId, member.getId())
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속방입니다"));

        Meeting meeting = mate.getMeeting();
        Eta mateEta = findByMateId(mate.getId());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime updatedAt = mateEta.getUpdatedAt().plusMinutes(10);

        //보내야 할 때 -> update
        if (updatedAt.isAfter(now) || updatedAt.isEqual(now)) {
            RouteTime routeTime = routeService.calculateRouteTime(mate.getOrigin(), meeting.getTarget());
            if (routeTime.getMinutes() == 0) {
                double meter = DistanceCalculator.calculate(
                        Double.valueOf(mateEtaRequest.currentLatitude()),
                        Double.valueOf(mateEtaRequest.currentLongitude()),
                        Double.valueOf(meeting.getTarget().getLatitude()),
                        Double.valueOf(meeting.getTarget().getLongitude())
                );
                if (meter <= 300) {
                    mateEta.updateArrived();
                }
            }
            mateEta.updateRemainingMinutes(routeTime.getMinutes());
        }

        List<Eta> etas = etaRepository.findAllByMeetingId(meetingId);
        List<MateEtaResponse> mateEtaResponses = new ArrayList<>();
        for (Eta eta : etas) {
            Duration duration = Duration.between(eta.getUpdatedAt(), LocalDateTime.now());
            long minutesDifference = duration.toMinutes();
            long returnTime = eta.getRemainingMinutes() - minutesDifference;
            mateEtaResponses.add(
                    new MateEtaResponse(
                            eta.getMate().getNicknameValue(),
                            EtaStatus.from(eta, LocalDateTime.of(meeting.getDate(), meeting.getTime()),
                                    mateEtaRequest.isMissing()),
                            returnTime
                    )
            );
        }

        //3. mate들 status 계산
        return new MateEtaResponses(mateEtaResponses);
    }

    private Eta findByMateId(Long mateId) {
        return etaRepository.findByMateId(mateId)
                .orElseThrow(() -> new OdyNotFoundException("ETA 정보를 찾을 수 없습니다"));
    }
}
