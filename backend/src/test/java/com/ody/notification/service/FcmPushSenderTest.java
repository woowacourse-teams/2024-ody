package com.ody.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import com.google.firebase.messaging.Message;
import com.ody.common.BaseServiceTest;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.notification.domain.message.GroupMessage;
import com.ody.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

class FcmPushSenderTest extends BaseServiceTest {

    @Autowired
    private FcmPushSender fcmPushSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("출발 알림이 푸시 알림을 보내고, DONE 상태로 변경한다.")
    @Test
    void sendPushNotificationSuccess() {
        Message message = Mockito.mock(Message.class);
        GroupMessage groupMessage = new GroupMessage(message);
        Notification pendingNotification = fixtureGenerator.generateNotification(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.PENDING
        );

        fcmPushSender.sendGroupMessage(groupMessage, pendingNotification);

        Notification notificationAfterSend = notificationRepository.findById(pendingNotification.getId()).get();

        assertAll(
                () -> Mockito.verify(firebaseMessaging, Mockito.times(1)).send(any(Message.class)),
                () -> assertThat(notificationAfterSend.getStatus()).isEqualTo(NotificationStatus.DONE)
        );
    }
}
