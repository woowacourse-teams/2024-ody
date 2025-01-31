package com.ody.common;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ody.auth.service.AppleRevokeTokenClient;
import com.ody.auth.service.KakaoAuthUnlinkClient;
import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmEventListener;
import com.ody.route.service.RouteClientCircuitBreaker;
import com.ody.route.domain.ApiCall;
import com.ody.route.domain.ClientType;
import com.ody.route.repository.ApiCallRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@Import({TestRouteConfig.class, TestAuthConfig.class, FixtureGeneratorConfig.class, RedisTestContainersConfig.class})
@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FirebaseMessaging firebaseMessaging;

    @MockBean
    protected FcmEventListener fcmEventListener;
  
    @MockBean
    protected RouteClientCircuitBreaker routeClientCircuitBreaker;

    @Autowired
    protected ApplicationEvents applicationEvents;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    protected FixtureGenerator fixtureGenerator;

    @Autowired
    private ApiCallRepository apiCallRepository;

    protected DtoGenerator dtoGenerator = new DtoGenerator();

    @SpyBean
    protected KakaoAuthUnlinkClient kakaoAuthUnlinkClient;

    @SpyBean
    protected AppleRevokeTokenClient appleRevokeTokenClient;

    @BeforeEach
    void setUp() {
        doNothing().when(kakaoAuthUnlinkClient).unlink(anyString());
        doNothing().when(appleRevokeTokenClient).unlink(anyString());
    }

    @BeforeEach
    void setUp() {
        databaseCleaner.cleanUp();
        applicationEvents.clear();
        apiCallRepository.save(new ApiCall(ClientType.ODSAY, 0, LocalDate.now()));
        apiCallRepository.save(new ApiCall(ClientType.GOOGLE, 0, LocalDate.now()));
    }
}

