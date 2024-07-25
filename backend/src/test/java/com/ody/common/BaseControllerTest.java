package com.ody.common;

import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmSubscriber;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FcmSubscriber fcmSubscriber;

    @Autowired
    private DatabaseCleaner databaseCleaner;

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
