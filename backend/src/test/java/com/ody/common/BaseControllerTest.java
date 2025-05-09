package com.ody.common;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.auth.JwtTokenProvider;
import com.ody.notification.config.FcmConfig;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.repository.ApiCallRepository;
import io.restassured.RestAssured;
import java.time.LocalDate;
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
    protected ApiCallRepository apiCallRepository;

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
        databaseCleaner.clear();
        apiCallRepository.save(new ApiCall(ClientType.ODSAY, 0, LocalDate.now()));
        apiCallRepository.save(new ApiCall(ClientType.GOOGLE, 0, LocalDate.now()));
    }
}
