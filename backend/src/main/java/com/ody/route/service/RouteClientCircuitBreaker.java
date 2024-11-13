package com.ody.route.service;

import com.ody.common.redis.CustomRedisTemplate;
import com.ody.route.domain.RouteClientKey;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class RouteClientCircuitBreaker {

    private static final int MIN_MINUTES = 0;
    private static final int MAX_MINUTES = 59;
    private static final int MAX_FAIL_COUNT = 3;
    private static final Duration FAIL_MINUTES_TTL = Duration.ofMinutes(31);
    private static final Duration BLOCK_HOUR_TTL = Duration.ofHours(3);

    private final CustomRedisTemplate redisTemplate;

    public void recordFailCountInMinutes(RouteClient routeClient) {
        String failClientKey = RouteClientKey.getFailKey(routeClient);
        int failedMinutes = LocalDateTime.now().getMinute();
        redisTemplate.opsForValue().setBit(failClientKey, failedMinutes, true);
        redisTemplate.expire(failClientKey, FAIL_MINUTES_TTL);

        int failureCount = redisTemplate.getBitCount(failClientKey, MIN_MINUTES, MAX_MINUTES);
        log.warn("{} 요청 실패 횟수 : {}", failClientKey, failureCount);
    }

    public void determineBlock(RouteClient routeClient) {
        String failClientKey = RouteClientKey.getFailKey(routeClient);
        String blockKey = RouteClientKey.getBlockKey(routeClient);
        if (exceedFailCount(failClientKey)) {
            block(blockKey);
            clearFailCount(failClientKey);
        }
    }

    private boolean exceedFailCount(String failCountKey) {
        int failureCount = redisTemplate.getBitCount(failCountKey, MIN_MINUTES, MAX_MINUTES);
        return failureCount >= MAX_FAIL_COUNT;
    }

    private void block(String blockKey) {
        redisTemplate.opsForValue().set(blockKey, "1");
        redisTemplate.expire(blockKey, BLOCK_HOUR_TTL);
        log.warn("{}가 차단되었습니다. 해제 예정 시간 : {}", blockKey, LocalDateTime.now().plus(BLOCK_HOUR_TTL));
    }

    private void clearFailCount(String failCountKey) {
        redisTemplate.unlink(failCountKey);
    }

    public boolean isBlocked(RouteClient routeClient) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(RouteClientKey.getBlockKey(routeClient)));
    }
}
