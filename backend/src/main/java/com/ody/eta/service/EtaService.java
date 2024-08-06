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

    private final RouteService routeService;
    private final EtaRepository etaRepository;
    private final MateRepository mateRepository;

    public Eta saveFirstEtaOfMate(Mate mate, RouteTime routeTime) {
        return etaRepository.save(new Eta(mate, routeTime.getMinutes()));
    }

    @Transactional
    public MateEtaResponses findAllMateEtas(MateEtaRequest mateEtaRequest, Long meetingId, Member member) {
        Mate mate = mateRepository.findByMeetingIdAndMemberId(meetingId, member.getId())
                .orElseThrow(() -> new OdyNotFoundException("참여하고 있지 않는 약속방입니다"));

        Meeting meeting = mate.getMeeting();
        LocalDateTime meetingTime = LocalDateTime.of(meeting.getDate(), meeting.getTime()).withSecond(0).withNano(0);
        Eta mateEta = findByMateId(mate.getId());
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime updateAt = mateEta.getUpdatedAt();
        long duration = Duration.between(updateAt, now).toMinutes();

        double meter = DistanceCalculator.calculate(
                Double.valueOf(mateEtaRequest.currentLatitude()),
                Double.valueOf(mateEtaRequest.currentLongitude()),
                Double.valueOf(meeting.getTarget().getLatitude()),
                Double.valueOf(meeting.getTarget().getLongitude())
        );

        if (meter <= 300 && (now.isBefore(meetingTime) || now.isEqual(meetingTime))) {
            mateEta.updateArrived();
        }

        //약속시간으로부터 30분 전일 때는 조건 상관없이 오디세이 무조건 호출
        LocalDateTime firstCallTime = meetingTime.minusMinutes(30L);

        if (((now.isAfter(firstCallTime) || now.isEqual(firstCallTime)) || duration >= 10L) && !mateEta.isArrived()) {
            RouteTime routeTime = routeService.calculateRouteTime(mate.getOrigin(), meeting.getTarget());
            mateEta.updateRemainingMinutes(routeTime.getMinutes());
        }

        List<Eta> etas = etaRepository.findAllByMeetingId(meetingId);
        List<MateEtaResponse> mateEtaResponses = new ArrayList<>();
        for (Eta eta : etas) {
            long countdownMinutes = eta.countDownMinutes(now);
            mateEtaResponses.add(
                    new MateEtaResponse(
                            eta.getMate().getNicknameValue(),
                            EtaStatus.from(
                                    countdownMinutes,
                                    meetingTime,
                                    eta.isArrived(),
                                    mateEtaRequest.isMissing()),
                            countdownMinutes
                    )
            );
        }

        return new MateEtaResponses(mateEtaResponses);
    }

    private Eta findByMateId(Long mateId) {
        return etaRepository.findByMateId(mateId)
                .orElseThrow(() -> new OdyNotFoundException("ETA 정보를 찾을 수 없습니다"));
    }
}
