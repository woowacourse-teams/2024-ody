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

    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public void sendGeneralMessage2(Message message, Notification notification) {
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
