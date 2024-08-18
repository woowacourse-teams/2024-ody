package com.ody.common;

import com.ody.common.config.JpaAuditingConfig;
import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import com.ody.notification.service.FcmSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import({JpaAuditingConfig.class, TestRouteConfig.class})
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    protected FcmPushSender fcmPushSender;

    @MockBean
    protected FcmSubscriber fcmSubscriber;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void cleanUp() {
        databaseCleaner.cleanUp();
    }
}

