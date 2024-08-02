package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.domain.Notification;
import com.ody.notification.dto.request.FcmSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    @Transactional
    public void sendPushNotification(FcmSendRequest fcmSendRequest) {
        Notification notification = fcmSendRequest.notification();
        Message message = Message.builder()
                .putData("type", notification.getType().toString())
                .putData("nickname", notification.getMate().getNickname())
                .setTopic(fcmSendRequest.topic())
                .build();
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
