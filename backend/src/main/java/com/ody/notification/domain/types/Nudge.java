package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;

public class Nudge extends AbstractNotification {

    private static final NotificationType type = NotificationType.NUDGE;

    public Nudge(Mate nudgeMate) {
        super(nudgeMate, type, LocalDateTime.now(), NotificationStatus.DONE, null);
    }
}
