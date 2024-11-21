package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventListener {

    private final FcmPushSender fcmPushSender;
    private final FcmSubscriber fcmSubscriber;

    @Async
    @EventListener
    public void subscribeTopic(SubscribeEvent subscribeEvent) {
        FcmTopic topic = subscribeEvent.getTopic();
        DeviceToken deviceToken = subscribeEvent.getDeviceToken();
        fcmSubscriber.subscribeTopic(topic, deviceToken);
    }

    @Async
    @EventListener
    public void unSubscribeTopic(UnSubscribeEvent subscribeEvent) {
        FcmTopic topic = subscribeEvent.getTopic();
        DeviceToken deviceToken = subscribeEvent.getDeviceToken();
        fcmSubscriber.subscribeTopic(topic, deviceToken);
    }

    @Async
    @EventListener
    public void sendNoticeMessage(NoticeEvent noticeEvent) {
        GroupMessage groupMessage = noticeEvent.getGroupMessage();
        fcmPushSender.sendNoticeMessage(groupMessage);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)// 커밋 이후 발송
    public void sendPushMessage(PushEvent pushEvent) {
        Notification notification = pushEvent.getNotification();
        GroupMessage groupMessage = GroupMessage.from(notification);
        fcmPushSender.sendGeneralMessage(groupMessage.message(), notification);
    }

    @Async
    @EventListener
    public void sendNudgeMessage(NudgeEvent nudgeEvent) {
        Notification nudgeNotification = nudgeEvent.getNudgeNotification();
        Mate requestMate = nudgeEvent.getRequestMate();
        DirectMessage nudgeMessage = DirectMessage.createMessageToOther(requestMate, nudgeNotification);
        fcmPushSender.sendGeneralMessage(nudgeMessage.message(), nudgeNotification);
    }
}
