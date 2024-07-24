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

    private static final String TITLE = "오디 타이틀";
    public static final String BODY = "오디 바디";

    public String sendPushNotification(FcmSendRequest fcmSendRequest) {
        Message message = Message.builder()
                .setTopic(fcmSendRequest.topic())
                .setNotification(buildNotification())
                .putData("type", fcmSendRequest.notificationType().name())
                .putData("nickname", fcmSendRequest.nickname())
                .setAndroidConfig(buildAndroidConfig())
                .build();
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Fcm 메시지 전송 실패 : {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }

    private static Notification buildNotification() {
        return Notification.builder()
                .setTitle(TITLE)
                .setBody(BODY)
                .build();
    }

    private static AndroidConfig buildAndroidConfig() {
        AndroidNotification androidNotification = AndroidNotification.builder()
                .setTitle(TITLE)
                .setBody(BODY)
                .setClickAction("push_click")
                .build();

        return AndroidConfig.builder()
                .setNotification(androidNotification)
                .build();
    }
}
