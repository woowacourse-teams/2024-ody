package com.ody.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.auth.JwtTokenProvider;
import com.ody.notification.config.FcmConfig;
import com.ody.route.service.RouteClientManager;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({TestRouteConfig.class, TestAuthConfig.class, FixtureGeneratorConfig.class, RedisTestContainersConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FirebaseMessaging firebaseMessaging;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RouteClientManager routeClientManager;

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    protected DtoGenerator dtoGenerator = new DtoGenerator();

    @LocalServerPort
    private int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    @BeforeEach
    void setUp() {
        databaseCleaner.cleanUp();
        routeClientManager.initializeClientApiCalls();
    }
}
