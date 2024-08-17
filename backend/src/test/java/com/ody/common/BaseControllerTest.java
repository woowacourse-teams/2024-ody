package com.ody.common;

import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import com.ody.notification.service.FcmSubscriber;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestRouteConfig.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @MockBean
    protected FcmConfig fcmConfig;

    @MockBean
    protected FcmSubscriber fcmSubscriber;

    @MockBean
    protected FcmPushSender fcmPushSender;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @BeforeEach
    void databaseCleanUp() {
        databaseCleaner.cleanUp();
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }
}
