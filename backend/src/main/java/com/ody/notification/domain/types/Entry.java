package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;

public class Entry extends AbstractNotification {

    private static final NotificationType type = NotificationType.ENTRY;

    public Entry(Mate mate, FcmTopic fcmTopic) {
        super(mate, type, LocalDateTime.now(), NotificationStatus.DONE, fcmTopic);
    }
}

