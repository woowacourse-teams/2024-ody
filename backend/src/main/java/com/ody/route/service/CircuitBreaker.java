package com.ody.route.service;

import com.ody.common.redis.CustomRedisTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
class CircuitBreaker {

    private static final int MIN_MINUTES = 0;
    private static final int MAX_MINUTES = 59;
    private static final int MAX_FAIL_COUNT = 3;
    private static final Duration FAIL_MINUTES_TTL = Duration.ofMinutes(31);
    private static final Duration BLOCK_HOUR_TTL = Duration.ofHours(3);

    private final CustomRedisTemplate redisTemplate;

    public void recordFailCountInMinutes(String failCountKey) {
        int failedMinutes = LocalDateTime.now().getMinute();
        redisTemplate.opsForValue().setBit(failCountKey, failedMinutes, true);
        redisTemplate.expire(failCountKey, FAIL_MINUTES_TTL);

        Long failureCount = redisTemplate.getBitCount(failCountKey, MIN_MINUTES, MAX_MINUTES);
        log.warn("{} 요청 실패 횟수 : {}", failCountKey, failureCount);
    }

    public void determineBlock(String failCountKey, String blockKey) {
        if (exceedFailCount(failCountKey)) {
            block(blockKey);
            clearFailCount(failCountKey);
        }
    }

    private boolean exceedFailCount(String failCountKey) {
        Long failureCount = redisTemplate.getBitCount(failCountKey, MIN_MINUTES, MAX_MINUTES);
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

    public boolean isBlocked(String blockKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(blockKey));
    }
}
