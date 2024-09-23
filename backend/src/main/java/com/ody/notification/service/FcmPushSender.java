package com.ody.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.DirectMessage;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.domain.message.NoticeMessage;
import com.ody.notification.dto.request.FcmGroupSendRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    @Transactional
    public void sendPushNotification(FcmGroupSendRequest fcmGroupSendRequest) {
        Notification notification = fcmGroupSendRequest.notification();
        if (notification.isStatusDismissed()) {
            return;
        }
        GroupMessage groupMessage = GroupMessage.from(notification);
        sendGeneralMessage(groupMessage.message(), notification);
    }

    public void sendNudgeMessage(Notification notification, DirectMessage directMessage) {
        sendGeneralMessage(directMessage.message(), notification);
    }

    private void sendGeneralMessage(Message message, Notification notification) {
        try {
            FirebaseMessaging.getInstance().send(message);
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

    public void sendNoticeMessage(NoticeMessage noticeMessage) {
        try {
            FirebaseMessaging.getInstance().send(noticeMessage.message());
        } catch (FirebaseMessagingException exception) {
            log.error("FCM 공지 전송 실패 : {}", exception.getMessage());
            throw new OdyServerErrorException(exception.getMessage());
        }
    }
}
