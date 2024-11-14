package com.ody.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmSubscriber;
import com.ody.route.service.RouteClientCircuitBreaker;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({TestRouteConfig.class, TestAuthConfig.class, FixtureGeneratorConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FirebaseMessaging firebaseMessaging;

    @MockBean
    protected RouteClientCircuitBreaker routeClientCircuitBreaker;

    @MockBean
    protected FcmSubscriber fcmSubscriber;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    protected DtoGenerator dtoGenerator = new DtoGenerator();

    @BeforeEach
    void cleanUp() {
        databaseCleaner.cleanUp();
    }
}

