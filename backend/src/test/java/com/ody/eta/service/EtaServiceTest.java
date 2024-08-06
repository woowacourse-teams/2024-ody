package com.ody.eta.service;

import static com.ody.common.Fixture.TARGET_LOCATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.eta.domain.Eta;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.eta.dto.response.MateEtaResponse;
import com.ody.eta.dto.response.MateEtaResponses;
import com.ody.eta.repository.EtaRepository;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.repository.MeetingRepository;
import com.ody.member.domain.Member;
import com.ody.member.repository.MemberRepository;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class EtaServiceTest extends BaseServiceTest {

    @MockBean
    private RouteService routeservice;

    @Autowired
    private EtaRepository etaRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MateRepository mateRepository;

    @Autowired
    private EtaService etaService;

    @DisplayName("오디세이 호출 여부 테스트")
    @Nested
    class OdsayCallTest {

        @DisplayName("오디세이를 호출한지 10분이 지났다면 새로운 소요시간을 오디세이로부터 응답받는다")
        @Test
        void callOdsayWhenDurationIsMoreThan10Minutes() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Member member = memberRepository.save(Fixture.MEMBER1);
            Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
            Mate mate = mateRepository.save(
                    new Mate(odyMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
            );
            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(11L);
            etaRepository.save(new Eta(mate, 30L, LocalDateTime.now(), updateTime));
            MateEtaRequest mateEtaRequest = new MateEtaRequest(false, origin.getLatitude(), origin.getLongitude());

            BDDMockito.when(routeservice.calculateRouteTime(any(), any()))
                    .thenReturn(new RouteTime(10L));

            etaService.findAllMateEtas(mateEtaRequest, odyMeeting.getId(), member);

            BDDMockito.verify(routeservice, Mockito.times(1)).calculateRouteTime(any(), any());
        }

        @DisplayName("오디세이를 호출한지 10분이 지나지 않았다면 오디세이를 호출하지 않는다.")
        @Test
        void callOdsayWhenDurationIsLessThan10Minutes() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Member member = memberRepository.save(Fixture.MEMBER1);
            Meeting odyMeeting = meetingRepository.save(Fixture.ODY_MEETING);
            Mate mate = mateRepository.save(
                    new Mate(odyMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
            );
            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(9L);
            etaRepository.save(new Eta(mate, 30L, LocalDateTime.now(), updateTime));
            MateEtaRequest mateEtaRequest = new MateEtaRequest(false, origin.getLatitude(), origin.getLongitude());

            etaService.findAllMateEtas(mateEtaRequest, odyMeeting.getId(), member);

            BDDMockito.verify(routeservice, Mockito.never()).calculateRouteTime(any(), any());
        }

        @DisplayName("약속 시간 30분에 첫번째 오디세이 호출이 시작된다")
        @Test
        void callFistOdsayWhen30minutesAgo() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Member member = memberRepository.save(Fixture.MEMBER1);
            LocalDateTime thirtyMinutesLater = LocalDateTime.now().plusMinutes(30L);
            Meeting meeting = new Meeting(
                    "오디",
                    thirtyMinutesLater.toLocalDate(),
                    thirtyMinutesLater.toLocalTime(),
                    TARGET_LOCATION,
                    "초대코드"
            );
            Meeting thirtyMinutesLaterMeeting = meetingRepository.save(meeting);

            Mate mate = mateRepository.save(
                    new Mate(thirtyMinutesLaterMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 10L)
            );
            etaRepository.save(new Eta(mate, 30L));
            MateEtaRequest mateEtaRequest = new MateEtaRequest(false, origin.getLatitude(), origin.getLongitude());

            BDDMockito.when(routeservice.calculateRouteTime(any(), any()))
                    .thenReturn(new RouteTime(10L));

            etaService.findAllMateEtas(mateEtaRequest, thirtyMinutesLaterMeeting.getId(), member);

            BDDMockito.verify(routeservice, Mockito.times(1)).calculateRouteTime(any(), any());
        }
    }

    @DisplayName("현재 시간 <= 약속 시간 && 직선거리가 300m 이내 일 경우 도차 상태로 업데이트한다.")
    @Test
    void findAllMateEtas() {
        Location origin = Fixture.ORIGIN_LOCATION;
        Member member = memberRepository.save(Fixture.MEMBER1);
        LocalDateTime now = LocalDateTime.now();
        Meeting meeting = new Meeting(
                "오디",
                now.toLocalDate(),
                now.toLocalTime(),
                origin,
                "초대코드"
        );
        Meeting thirtyMinutesLaterMeeting = meetingRepository.save(meeting);

        Mate mate = mateRepository.save(
                new Mate(thirtyMinutesLaterMeeting, member, new Nickname("은별"), Fixture.ORIGIN_LOCATION, 0L)
        );
        etaRepository.save(new Eta(mate, 30L));
        MateEtaRequest mateEtaRequest = new MateEtaRequest(false, origin.getLatitude(), origin.getLongitude());

        MateEtaResponses etas = etaService.findAllMateEtas(mateEtaRequest, thirtyMinutesLaterMeeting.getId(), member);
        MateEtaResponse mateEtaResponse = etas.mateEtas().stream()
                .filter(response -> response.nickname().equals(mate.getNicknameValue()))
                .findAny()
                .get();

        assertThat(mateEtaResponse.status()).isEqualTo(EtaStatus.ARRIVED);
    }
}
