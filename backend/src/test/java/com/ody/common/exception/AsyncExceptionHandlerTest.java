package com.ody.common.exception;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.firebase.messaging.Message;
import com.ody.common.BaseControllerTest;
import com.ody.notification.service.FcmSubscriber;
import com.ody.notification.service.event.SubscribeEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

class AsyncExceptionHandlerTest extends BaseControllerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private FcmSubscriber fcmSubscriber;

    @MockBean
    private AsyncExceptionHandler asyncExceptionHandler;

    @DisplayName("비동기 메서드에서 발생한 에러를 핸들링한다")
    @Test
    void handleUncaughtException() throws InterruptedException {
        ApplicationEvent subscribeEvent = mock(SubscribeEvent.class);
        Exception stubException = new RuntimeException();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        doThrow(stubException)
                .when(fcmSubscriber)
                .subscribeTopic(any(), any());

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;

        }).when(asyncExceptionHandler).handleUncaughtException(any(), any(), any(), any());

        eventPublisher.publishEvent(subscribeEvent);
        countDownLatch.await(3L, TimeUnit.SECONDS);

        verify(asyncExceptionHandler, times(1))
                .handleUncaughtException(any(), any(), eq(subscribeEvent));
    }
}
