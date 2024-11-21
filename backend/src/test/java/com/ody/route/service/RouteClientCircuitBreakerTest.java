package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseRedisTest;
import com.ody.route.domain.RouteClientKey;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

class RouteClientCircuitBreakerTest extends BaseRedisTest {

    @Autowired
    private RouteClientCircuitBreaker circuitBreaker;

    @MockBean
    @Qualifier("odsay")
    private RouteClient routeClient;

    @DisplayName("실패 횟수를 기록하고 TTL을 31분으로 설정한다.")
    @Test
    void recordFailCountInMinutes() {
        circuitBreaker.recordFailCountInMinutes(routeClient);

        String failClientKey = RouteClientKey.getFailKey(routeClient);
        int failureCount = redisTemplate.getKeyCount(failClientKey);
        Long ttlMinutes = redisTemplate.getExpire(failClientKey, TimeUnit.MINUTES);

        // 지연 시간 때문에 TTL을 범위로 테스트
        assertAll(
                () -> assertThat(failureCount).isEqualTo(1),
                () -> assertThat(ttlMinutes).isBetween(30L, 31L)
        );
    }

    @DisplayName("실패 횟수가 3회 이상이면 block을 결정하고 실패 횟수를 초기화한다.")
    @Test
    void determineBlock() {
        circuitBreaker.recordFailCountInMinutes(routeClient);
        circuitBreaker.recordFailCountInMinutes(routeClient);
        circuitBreaker.recordFailCountInMinutes(routeClient);
        circuitBreaker.determineBlock(routeClient);

        String failClientKey = RouteClientKey.getFailKey(routeClient);
        int failCount = redisTemplate.getKeyCount(failClientKey);
        Boolean blocked = redisTemplate.hasKey(RouteClientKey.getBlockKey(routeClient));

        assertAll(
                () -> assertThat(failCount).isZero(),
                () -> assertThat(blocked).isTrue()
        );
    }

    @DisplayName("RouteClient에 해당하는 block 키가 존재하면 true를 반환한다.")
    @Test
    void isBlocked() {
        String blockKey = RouteClientKey.getBlockKey(routeClient);
        redisTemplate.opsForValue().set(blockKey, "1");

        boolean blocked = circuitBreaker.isBlocked(routeClient);

        assertThat(blocked).isTrue();
    }
}
