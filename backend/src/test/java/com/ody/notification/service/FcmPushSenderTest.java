package com.ody.notification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import com.google.firebase.messaging.Message;
import com.ody.common.BaseServiceTest;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
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

    @DisplayName("출발 알림이 DISMISSED 상태가 아니면 이면 푸시 알림을 보내고, DONE 상태로 변경한다.")
    @Test
    void sendPushNotificationSuccess() {
        Notification pendingNotification = fixtureGenerator.generateNotification(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.PENDING
        );

        fcmPushSender.sendPushNotification(pendingNotification);

        Notification notificationAfterSend = notificationRepository.findById(pendingNotification.getId()).get();

        assertAll(
                () -> Mockito.verify(firebaseMessaging, Mockito.times(1)).send(any(Message.class)),
                () -> assertThat(notificationAfterSend.getStatus()).isEqualTo(NotificationStatus.DONE)
        );
    }

    @DisplayName("DISMISSED 상태이면 푸시 알림을 보내지 않는다.")
    @Test
    void sendPushNotificationFailure() {
        Notification dismissedNotification = fixtureGenerator.generateNotification(
                NotificationType.DEPARTURE_REMINDER,
                NotificationStatus.DISMISSED
        );

        fcmPushSender.sendPushNotification(dismissedNotification);

        Notification notificationAfterSend = notificationRepository.findById(dismissedNotification.getId()).get();

        assertAll(
                () -> Mockito.verifyNoInteractions(firebaseMessaging),
                () -> assertThat(notificationAfterSend.getStatus()).isEqualTo(NotificationStatus.DISMISSED)
        );
    }
}
