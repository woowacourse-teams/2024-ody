package com.ody.common.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseControllerTest;
import com.ody.common.BaseServiceTest;
import com.ody.common.FixtureGeneratorConfig;
import com.ody.common.TestAuthConfig;
import com.ody.common.TestRouteConfig;
import com.ody.notification.service.FcmEventListener;
import com.ody.notification.service.FcmSubscriber;
import com.ody.notification.service.event.SubscribeEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.RecordApplicationEvents;

class AsyncExceptionHandlerTest extends BaseControllerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private FcmSubscriber fcmSubscriber;

    @MockBean
    private AsyncExceptionHandler asyncExceptionHandler;

    @Test
    void handleUncaughtException() throws InterruptedException {
        ApplicationEvent subscribeEvent = mock(SubscribeEvent.class);
        Exception stubException = new RuntimeException();
        doThrow(stubException)
                .when(fcmSubscriber)
                .subscribeTopic(any(), any());

        eventPublisher.publishEvent(subscribeEvent);
        Thread.sleep(1000); // 비동기 메서드가 실행될때까지 대기

        verify(asyncExceptionHandler, times(1))
                .handleUncaughtException(any(), any(), eq(subscribeEvent));
    }
}
