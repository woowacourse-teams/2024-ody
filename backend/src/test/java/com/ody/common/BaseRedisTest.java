package com.ody.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.eta.repository.EtaSchedulingRedisTemplate;
import com.ody.route.repository.RouteClientRedisTemplate;
import com.ody.notification.config.FcmConfig;
import java.util.Objects;
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
    protected RouteClientRedisTemplate redisTemplate;

    @Autowired
    protected EtaSchedulingRedisTemplate etaSchedulingRedisTemplate;

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void init() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory())
                .getConnection()
                .serverCommands()
                .flushAll();
    }
}
