package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseServiceTest;
import com.ody.common.RedisTestContainersConfig;
import com.ody.route.domain.ClientType;
import com.ody.route.dto.ApiCallCountResponse;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

class ApiCallServiceTest extends BaseServiceTest {

    @Autowired
    private ApiCallService apiCallService;

    @DisplayName("오늘 Odsay API 호출 횟수를 조회한다.")
    @Test
    void countOdsayApiCallSuccess() {
        LocalDate now = LocalDate.now();
        fixtureGenerator.generateApiCall(ClientType.ODSAY, 2, now);
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 3, now);

        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.ODSAY);
        ApiCallCountResponse expected = new ApiCallCountResponse(2);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("오늘 Odsay API 호출 횟수가 없으면 0을 반환한다.")
    @Test
    void countOdsayApiCallFailure() {
        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.ODSAY);
        ApiCallCountResponse expected = new ApiCallCountResponse(0);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이번달 Google API 호출 횟수를 조회한다.")
    @Test
    void countGoogleApiCallSuccess() {
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 1, lastMonth);
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 3, now);
        fixtureGenerator.generateApiCall(ClientType.ODSAY, 2, now);

        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.GOOGLE);
        ApiCallCountResponse expected = new ApiCallCountResponse(3);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이번달 Odsay API 호출 횟수가 없으면 0을 반환한다.")
    @Test
    void countGoogleApiCallFailure() {
        ApiCallCountResponse actual = apiCallService.countApiCall(ClientType.GOOGLE);
        ApiCallCountResponse expected = new ApiCallCountResponse(0);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("이번 달 처음 Google API 호출이면 count=1 저장한다.")
    @Test
    void firstIncreaseCountByRouteClient() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 100, firstDayOfMonth.minusDays(1));
        fixtureGenerator.generateApiCall(ClientType.ODSAY, 3, firstDayOfMonth);

        ClientType clientType = new StubGoogleRouteClient().getClientType();
        apiCallService.increaseCountByClientType(clientType);

        int actual = apiCallService.countApiCall(ClientType.GOOGLE).count();
        assertThat(actual).isEqualTo(1);
    }

    @DisplayName("오늘 처음 Odsay API 호출이 아니면 count+1 한다.")
    @Test
    void increaseCountByRouteClient() {
        LocalDate now = LocalDate.now();
        fixtureGenerator.generateApiCall(ClientType.GOOGLE, 100, now);
        fixtureGenerator.generateApiCall(ClientType.ODSAY, 3, now);

        ClientType clientType = new StubOdsayRouteClient().getClientType();
        apiCallService.increaseCountByClientType(clientType);

        int actual = apiCallService.countApiCall(ClientType.ODSAY).count();
        assertThat(actual).isEqualTo(4);
    }

    @DisplayName("Redisson 분산락 동시성 테스트")
    @Import(RedisTestContainersConfig.class)
    @Nested
    public class RedissonDistributedLockTest {

        private static final int TOTAL_REQUESTS = 100;

        @SpyBean
        private ApiCallService apiCallService;

        @DisplayName("100명의 사용자가 동시에 API를 호출할 경우 정확히 count+100 한다.")
        @Test
        void concurrencyIncreaseCountByRouteClient() throws InterruptedException {

            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);
            ClientType clientType = new StubOdsayRouteClient().getClientType();

            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        apiCallService.increaseCountByClientType(clientType);
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

        @DisplayName("동시에 100개 요청 중 절반이 예외가 발생하면 해당 트랜잭션은 롤백되어 count+50 한다.")
        @Test
        void concurrencyIncreaseCountByRouteClientAndRollBack() throws InterruptedException {
            ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_REQUESTS);
            CountDownLatch countDownLatch = new CountDownLatch(TOTAL_REQUESTS);
            ClientType clientType = new StubOdsayRouteClient().getClientType();

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger totalCount = new AtomicInteger(0);

            Mockito.doAnswer(invocation -> {
                int currentCount = totalCount.getAndIncrement();
                if (currentCount % 2 == 0) {
                    successCount.incrementAndGet();
                    return invocation.callRealMethod();
                }
                throw new RuntimeException();
            }).when(apiCallService).increaseCountByClientType(clientType);

            for (int i = 1; i <= TOTAL_REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        apiCallService.increaseCountByClientType(clientType);
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
