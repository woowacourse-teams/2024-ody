package com.ody.common;

import com.ody.notification.config.FcmConfig;
import com.ody.notification.service.FcmPushSender;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class BaseServiceTest {

    @MockBean
    private FcmConfig fcmConfig;

    @MockBean
    private FcmPushSender fcmPushSender;

    public FcmPushSender getFcmPushSender() {
        return fcmPushSender;
    }
}
