package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;

public abstract class AbstractNotification {

    private final Mate mate;
    private final NotificationType type;
    private final LocalDateTime sendAt;
    private final NotificationStatus status;
    private final FcmTopic fcmTopic;

    protected AbstractNotification(
            Mate mate,
            LocalDateTime sendAt,
            NotificationStatus status,
            FcmTopic fcmTopic
    ) {
        this.mate = mate;
        this.type = getType();
        this.sendAt = calculateSendAt(sendAt);
        this.status = status;
        this.fcmTopic = fcmTopic;
    }

    private static LocalDateTime calculateSendAt(LocalDateTime sendAt) {
        if (sendAt.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return sendAt;
    }

    abstract NotificationType getType();

    public Notification toNotification() {
        return new Notification(null, mate, type, sendAt, status, fcmTopic);
    }
}
