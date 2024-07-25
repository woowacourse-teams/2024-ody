package com.ody.notification.service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ody.notification.dto.request.FcmSendRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FcmPushSender {

    public String sendPushNotification(FcmSendRequest fcmSendRequest) {
        Message message = Message.builder()
                .setTopic(fcmSendRequest.topic())
                .putData("type", fcmSendRequest.notificationType().name())
                .putData("nickname", fcmSendRequest.nickname())
                .build();
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
