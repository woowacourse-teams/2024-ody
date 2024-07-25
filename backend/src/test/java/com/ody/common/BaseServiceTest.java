package com.ody.common;

import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FcmPushSender fcmPushSender;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanUp() {
        databaseCleaner.cleanUp();
    }

    public FcmPushSender getFcmPushSender() {
        return fcmPushSender;
    }
}
