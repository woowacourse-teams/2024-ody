package com.ody.notification.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ody.common.BaseServiceTest;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

class FcmEventListenerTest extends BaseServiceTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private FcmEventListener fcmEventListener;

    @MockBean
    private FcmPushSender fcmPushSender;

    @DisplayName("SubscribeEvent 발생 시, 특정 주제 구독 로직을 실행한다")
    @Test
    void subscribeTopic() {
        SubscribeEvent subscribeEvent = mock(SubscribeEvent.class);

        eventPublisher.publishEvent(subscribeEvent);

        verify(fcmEventListener, times(1)).subscribeTopic(eq(subscribeEvent));
    }

    @DisplayName("UnsubscribeEvent 발생 시, 주제 구독 해제 로직을 실행한다")
    @Test
    void unSubscribeTopic() {
        UnSubscribeEvent unSubscribeEvent = mock(UnSubscribeEvent.class);

        eventPublisher.publishEvent(unSubscribeEvent);

        verify(fcmEventListener, times(1)).unSubscribeTopic(eq(unSubscribeEvent));
    }

    @DisplayName("NoticeEvent 발생 시, 공지 알림 발송 로직을 실행한다")
    @Test
    void sendNoticeMessage() {
        NoticeEvent noticeEvent = mock(NoticeEvent.class);

        eventPublisher.publishEvent(noticeEvent);

        verify(fcmEventListener, times(1)).sendNoticeMessage(eq(noticeEvent));
    }

    @DisplayName("PushEvent 발생 시, 푸시 알림 발송 로직을 실행한다")
    @Test
    void sendPushMessage() {
        PushEvent pushEvent = mock(PushEvent.class);

        eventPublisher.publishEvent(pushEvent);

        verify(fcmEventListener, times(1)).sendPushMessage(eq(pushEvent));
    }

    @DisplayName("NudgeEvent 발생 시, 넛지 알림 발송 로직을 실행한다")
    @Test
    void sendNudgeMessage() {
        NudgeEvent nudgeEvent = mock(NudgeEvent.class);

        eventPublisher.publishEvent(nudgeEvent);

        verify(fcmEventListener, times(1)).sendNudgeMessage(eq(nudgeEvent));
    }
}
