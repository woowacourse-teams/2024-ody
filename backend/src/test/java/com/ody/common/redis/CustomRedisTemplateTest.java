package com.ody.common.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.common.RedisTestContainersConfig;
import com.ody.notification.config.FcmConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(RedisTestContainersConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CustomRedisTemplateTest {

    private static final String TEST_KEY = "test";

    @Autowired
    private CustomRedisTemplate redisTemplate;

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @DisplayName("범위 내의 비트 개수를 반환한다.")
    @Test
    void getBitCount() {
        redisTemplate.opsForValue().setBit(TEST_KEY, 0, true);
        redisTemplate.opsForValue().setBit(TEST_KEY, 1, true);
        redisTemplate.opsForValue().setBit(TEST_KEY, 2, true);

        int bitCount = redisTemplate.getBitCount(TEST_KEY, 0, 1);

        assertThat(bitCount).isEqualTo(2);
    }
}
