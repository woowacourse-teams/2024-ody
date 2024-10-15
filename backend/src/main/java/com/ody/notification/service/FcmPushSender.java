package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.repository.NotificationRepository;
import com.ody.util.TimeUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private final NotificationRepository notificationRepository;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void sendPushNotification(Notification notification) {
        Notification savedNotification = notificationRepository.findById(notification.getId())
                .orElse(notification); // noti 저장과 같은 트랜잭션에서 실행되는 경우, 즉시 findById 할 수 없어 기존 noti 사용

        if (savedNotification.isStatusDismissed()) {
            log.info("DISMISSED 상태 푸시 알림 전송 스킵 : {}", savedNotification);
            return;
        }
        GroupMessage groupMessage = GroupMessage.from(savedNotification);
        sendGeneralMessage(groupMessage.message(), savedNotification);
    }

    public void sendNudgeMessage(Notification notification, DirectMessage directMessage) {
        sendGeneralMessage(directMessage.message(), notification);
    }

    private void sendGeneralMessage(Message message, Notification notification) {
        try {
            firebaseMessaging.send(message);
            updateDepartureReminderToDone(notification);
        } catch (FirebaseMessagingException exception) {
            log.error("FCM 알림(ID : {}) 전송 실패 : {}", notification.getId(), exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }

    private void updateDepartureReminderToDone(Notification notification) {
        if (notification.isDepartureReminder()) {
            notification.updateStatusToDone();
            log.info("{} 타입 알림(ID : {}) 상태 업데이트", notification.getType(), notification.getId());
        }
    }

    public void sendNoticeMessage(GroupMessage groupMessage) {
        try {
            firebaseMessaging.send(groupMessage.message());
            log.info("공지 알림 전송 | 전송 시간 : {}", Instant.now().atZone(TimeUtil.KST_OFFSET));
        } catch (FirebaseMessagingException exception) {
            log.error("FCM 공지 전송 실패 : {}", exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }
}
