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

    private static final String TEST_TITLE = "오디 메시지 임시 타이틀";

    public String sendPushNotification(FcmSendRequest fcmSendRequest) {
        Message message = Message.builder()
                .setToken(fcmSendRequest.token())
                .setNotification(Notification.builder()
                        .setTitle(TEST_TITLE)
                        .setBody(fcmSendRequest.notificationType().name())
                        .build())
                .putData("type", fcmSendRequest.notificationType().name())
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setNotification(
                                        AndroidNotification.builder()
                                                .setTitle(TEST_TITLE)
                                                .setBody(fcmSendRequest.notificationType().name())
                                                .setClickAction("push_click")
                                                .build()
                                ).build()
                ).build();
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
