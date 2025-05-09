package com.ody.eta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import com.ody.common.BaseServiceTest;
import com.ody.common.Fixture;
import com.ody.eta.domain.EtaStatus;
import com.ody.eta.dto.request.MateEtaRequest;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Location;
import com.ody.meeting.domain.Meeting;
import com.ody.meeting.dto.response.MateEtaResponseV2;
import com.ody.meeting.dto.response.MateEtaResponsesV2;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.ApiCallService;
import com.ody.route.service.RouteService;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

class EtaServiceTest extends BaseServiceTest {

    @SpyBean
    private RouteService routeService;

    @Autowired
    private EtaService etaService;

    @Autowired
    private ApiCallService apiCallService;

    @DisplayName("오디세이 호출 여부 테스트")
    @Nested
    class OdsayCallTest {

        @DisplayName("오디세이를 호출한지 10분이 지났다면 새로운 소요시간을 오디세이로부터 응답받는다")
        @Test
        void callOdsayWhenDurationIsMoreThan10Minutes() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Meeting odyMeeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(odyMeeting, origin);

            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(11L);
            fixtureGenerator.generateEta(mate, 30L, updateTime);
            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, origin);

            Mockito.doReturn(new RouteTime(10L))
                    .when(routeService)
                    .calculateRouteTime(any(), any());

            etaService.findAllMateEtas(mateEtaRequest, mate);

            BDDMockito.verify(routeService).calculateRouteTime(any(), any());
        }

        @DisplayName("오디세이를 호출한지 10분이 지나지 않았다면 오디세이를 호출하지 않는다.")
        @Test
        void callOdsayWhenDurationIsLessThan10Minutes() {
            Location origin = Fixture.ORIGIN_LOCATION;
            Meeting odyMeeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(odyMeeting, origin);

            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(9L);
            fixtureGenerator.generateEta(mate, 30L, updateTime);
            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, origin);

            etaService.findAllMateEtas(mateEtaRequest, mate);

            BDDMockito.verify(routeService, Mockito.never()).calculateRouteTime(any(), any());
        }

        @DisplayName("약속 시간 30분 전에 첫번째 오디세이 호출이 시작된다")
        @Test
        void callFistOdsayWhen30minutesAgo() {
            Location origin = Fixture.ORIGIN_LOCATION;
            LocalDateTime thirtyMinutesLater = LocalDateTime.now().plusMinutes(30L);
            Meeting meeting = fixtureGenerator.generateMeeting(thirtyMinutesLater);
            Mate mate = fixtureGenerator.generateMate(meeting, origin);

            fixtureGenerator.generateEta(mate, 31L);
            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, origin);

            Mockito.doReturn(new RouteTime(31L))
                    .when(routeService)
                    .calculateRouteTime(any(), any());

            etaService.findAllMateEtas(mateEtaRequest, mate);

            BDDMockito.verify(routeService, Mockito.times(1)).calculateRouteTime(any(), any());
        }
    }

    @DisplayName("현재 시간 <= 약속 시간 && 직선거리가 300m 이내 일 경우 도착 상태로 업데이트한다.")
    @Test
    void findAllMateEtas() {
        Location target = Fixture.TARGET_LOCATION;
        LocalDateTime now = LocalDateTime.now();
        Meeting nowMeeting = fixtureGenerator.generateMeeting(now);
        Mate mate = fixtureGenerator.generateMate(nowMeeting, target);

        fixtureGenerator.generateEta(mate, 30L);
        MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, target);

        MateEtaResponsesV2 etas = etaService.findAllMateEtas(mateEtaRequest, mate);
        MateEtaResponseV2 mateEtaResponse = etas.mateEtas().stream()
                .filter(response -> response.mateId() == mate.getId())
                .findAny()
                .get();

        assertThat(mateEtaResponse.status()).isEqualTo(EtaStatus.ARRIVED);
    }

    @DisplayName("ETA 목록 조회 시 API 호출 카운팅 동시성 테스트")
    @Nested
    class ApiCallCountConcurrencyTest {

        private static final int TOTAL_REQUESTS = 100;

        @DisplayName("100명의 사용자가 동시에 ETA 목록 조회하여 API를 호출할 경우 정확히 count+100 한다.")
        @Test
        void concurrencyFindAllMateEtas() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);

            Meeting odyMeeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(odyMeeting, Fixture.ORIGIN_LOCATION);
            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(11L);
            fixtureGenerator.generateEta(mate, 30L, updateTime);
            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, Fixture.ORIGIN_LOCATION);

            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        etaService.findAllMateEtas(mateEtaRequest, mate);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await(3, TimeUnit.SECONDS);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);

            int actual = apiCallService.countApiCall(ClientType.ODSAY).count();

            assertThat(actual).isEqualTo(TOTAL_REQUESTS);
        }

        @DisplayName("100명의 사용자가 동시에 ETA 목록 조회하여 절반이 예외가 발생하면 해당 트랜잭션은 롤백되어 count+50 한다.")
        @Test
        void concurrencyFindAllMateEtasRollBack() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);

            Meeting odyMeeting = fixtureGenerator.generateMeeting();
            Mate mate = fixtureGenerator.generateMate(odyMeeting, Fixture.ORIGIN_LOCATION);
            LocalDateTime updateTime = LocalDateTime.now().minusMinutes(11L);
            fixtureGenerator.generateEta(mate, 30L, updateTime);
            MateEtaRequest mateEtaRequest = dtoGenerator.generateMateEtaRequest(false, Fixture.ORIGIN_LOCATION);

            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                final int index = i;
                executorService.execute(() -> {
                    try {
                        if (index % 2 == 0) {
                            throw new RuntimeException();
                        }
                        etaService.findAllMateEtas(mateEtaRequest, mate);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await(3, TimeUnit.SECONDS);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);

            int actual = apiCallService.countApiCall(ClientType.ODSAY).count();

            assertThat(actual).isEqualTo(TOTAL_REQUESTS / 2);
        }
    }
}
