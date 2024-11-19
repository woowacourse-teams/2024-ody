package com.ody.route.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.BaseRedisTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RouteClientRedisTemplateTest extends BaseRedisTest {

    private static final String TEST_KEY = "test";

    @DisplayName("key의 counter를 증가시킨다.")
    @Test
    void increment() {
        redisTemplate.increment(TEST_KEY);

        int keyCount = redisTemplate.getKeyCount(TEST_KEY);

        assertThat(keyCount).isEqualTo(1);
    }
}
