package com.ody.notification.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.ody.common.BaseServiceTest;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FcmEventListenerTest extends BaseServiceTest {

    @Autowired
    private FcmEventPublisher eventPublisher;

    @DisplayName("SubscribeEvent 발생 시, 특정 주제 구독 로직을 실행한다")
    @Test
    void subscribeTopic() {
        SubscribeEvent subscribeEvent = mock(SubscribeEvent.class);

        eventPublisher.publish(subscribeEvent);

        verify(fcmEventListener, times(1)).subscribeTopic(eq(subscribeEvent));
    }

    @DisplayName("UnsubscribeEvent 발생 시, 주제 구독 해제 로직을 실행한다")
    @Test
    void unSubscribeTopic() {
        UnSubscribeEvent unSubscribeEvent = mock(UnSubscribeEvent.class);

        eventPublisher.publish(unSubscribeEvent);

        verify(fcmEventListener, times(1)).unSubscribeTopic(eq(unSubscribeEvent));
    }

    @DisplayName("NoticeEvent 발생 시, 공지 알림 발송 로직을 실행한다")
    @Test
    void sendNoticeMessage() {
        NoticeEvent noticeEvent = mock(NoticeEvent.class);

        eventPublisher.publish(noticeEvent);

        verify(fcmEventListener, times(1)).sendNoticeMessage(eq(noticeEvent));
    }

    @DisplayName("PushEvent 발생 + 트랜잭션 커밋 이후, 푸시 알림 발송 로직을 실행한다")
    @Test
    void sendPushMessage() {
        PushEvent pushEvent = mock(PushEvent.class);

        eventPublisher.publishWithTransaction(pushEvent);

        verify(fcmEventListener, times(1)).sendPushMessage(eq(pushEvent));
    }

    @DisplayName("트랜잭션이 열리지 않으면, 푸시 알림 발송 로직이 실행되지 않는다")
    @Test
    void notEventTriggerWhenTransactionNotOpen() {
        PushEvent pushEvent = mock(PushEvent.class);

        eventPublisher.publish(pushEvent);

        verifyNoInteractions(fcmEventListener);
    }

    @DisplayName("NudgeEvent 발생 시, 넛지 알림 발송 로직을 실행한다")
    @Test
    void sendNudgeMessage() {
        NudgeEvent nudgeEvent = mock(NudgeEvent.class);

        eventPublisher.publish(nudgeEvent);

        verify(fcmEventListener, times(1)).sendNudgeMessage(eq(nudgeEvent));
    }
}
