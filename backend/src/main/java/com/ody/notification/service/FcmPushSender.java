package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.NudgePushMessage;
import com.ody.notification.domain.message.TopicPushMessage;
import com.ody.notification.dto.request.FcmTopicSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    @Transactional
    public void sendPushNotification(FcmTopicSendRequest fcmTopicSendRequest) {
        Notification notification = fcmTopicSendRequest.notification();
        TopicPushMessage topicPushMessage = new TopicPushMessage(notification);
        sendMessage(topicPushMessage.getMessage(), notification);
    }

    @Transactional
    public void sendNudgeMessage(Notification notification, NudgePushMessage nudgePushMessage) {
        sendMessage(nudgePushMessage.getMessage(), notification);
    }

    private void sendMessage(Message message, Notification notification) {
        try {
            FirebaseMessaging.getInstance().send(message);
            notification.updateStatusToDone();
            log.info("알림 상태 업데이트 : {}", notification);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }
}
