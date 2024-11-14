package com.ody.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.common.redis.CustomRedisTemplate;
import com.ody.notification.config.FcmConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import({RedisTestContainersConfig.class, TestRouteConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseRedisTest {

    @Autowired
    protected CustomRedisTemplate redisTemplate;

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void init() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }
}
