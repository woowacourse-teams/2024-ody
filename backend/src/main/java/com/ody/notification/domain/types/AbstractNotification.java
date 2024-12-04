package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.Notification;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class AbstractNotification {

    private final Long id;
    private final Mate mate;
    private final NotificationType type;
    private final LocalDateTime sendAt;
    private final NotificationStatus status;
    private final FcmTopic fcmTopic;

    public AbstractNotification(
            Mate mate,
            NotificationType type,
            LocalDateTime sendAt,
            NotificationStatus status,
            FcmTopic fcmTopic
    ) {
        this(null, mate, type, sendAt, status, fcmTopic);
    }

    public Notification toNotification() {
        return new Notification(id, mate, type, sendAt, status, fcmTopic);
    }
}
