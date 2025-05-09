package com.ody.notification.service;

import com.ody.mate.domain.Mate;
import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.AbstractMessage;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.service.event.NoticeEvent;
import com.ody.notification.service.event.NudgeEvent;
import com.ody.notification.service.event.PushEvent;
import com.ody.notification.service.event.SubscribeEvent;
import com.ody.notification.service.event.UnSubscribeEvent;
import com.ody.util.TimeUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventListener {

    private final FcmPushSender fcmPushSender;
    private final FcmSubscriber fcmSubscriber;

    @Async("fcmAsyncExecutor")
    @EventListener
    public void subscribeTopic(SubscribeEvent subscribeEvent) {
        FcmTopic topic = subscribeEvent.getTopic();
        DeviceToken deviceToken = subscribeEvent.getDeviceToken();
        fcmSubscriber.subscribeTopic(topic, deviceToken);
    }

    @Async("fcmAsyncExecutor")
    @EventListener
    public void unSubscribeTopic(UnSubscribeEvent subscribeEvent) {
        FcmTopic topic = subscribeEvent.getTopic();
        DeviceToken deviceToken = subscribeEvent.getDeviceToken();
        fcmSubscriber.unSubscribeTopic(topic, deviceToken);
    }

    @Async("fcmAsyncExecutor")
    @EventListener
    public <T extends AbstractMessage> void sendNoticeMessage(NoticeEvent<T> noticeEvent) {
        fcmPushSender.sendMessage(noticeEvent.getMessage());
        log.info(
                "{} 공지 알림 전송 | 전송 시간 : {}",
                noticeEvent.getNotice().getType(),
                Instant.now().atZone(TimeUtil.KST_OFFSET)
        );
    }

    @Async("fcmAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)// 커밋 이후 발송
    public void sendPushMessage(PushEvent pushEvent) {
        Notification notification = pushEvent.getNotification();
        GroupMessage groupMessage = pushEvent.getGroupMessage();
        fcmPushSender.sendGroupMessage(groupMessage, notification);
        log.info("푸시 알림 성공 - id : {}, 전송시간 : {}", notification.getId(), notification.getSendAt());
    }

    @Async("fcmAsyncExecutor")
    @EventListener
    public void sendNudgeMessage(NudgeEvent nudgeEvent) {
        Notification nudgeNotification = nudgeEvent.getNudgeNotification();
        Mate requestMate = nudgeEvent.getRequestMate();
        DirectMessage nudgeMessage = DirectMessage.createMessageToOther(requestMate, nudgeNotification);
        fcmPushSender.sendMessage(nudgeMessage);
        log.info("재촉하기 성공 - id : {}, 전송시간 : {}", nudgeNotification.getId(), nudgeNotification.getSendAt());
    }
}
