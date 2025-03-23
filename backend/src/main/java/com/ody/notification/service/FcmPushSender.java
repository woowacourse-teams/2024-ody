package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.AbstractMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void sendGroupMessage(GroupMessage groupMessage, Notification notification) {
        Notification savedNotification = findNotification(notification);
        sendMessage(groupMessage.getMessage());
        updateDepartureReminderToDone(savedNotification);
    }

    public void sendMessage(AbstractMessage abstractMessage) {
        sendMessage(abstractMessage.getMessage());
    }

    private void sendMessage(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("FCM 전송 실패 : {}", exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }

    private Notification findNotification(Notification notification) {
        return notificationRepository.findById(notification.getId())
                .orElse(notification);
    }

    private void updateDepartureReminderToDone(Notification notification) {
        if (notification.isDepartureReminder()) {
            notification.updateStatusToDone();
            log.info("{} 타입 알림(ID : {}) 상태 업데이트", notification.getType(), notification.getId());
        }
    }
}
