package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;

public class Nudge extends AbstractNotification {

    public Nudge(Mate nudgeMate) {
        super(nudgeMate, LocalDateTime.now(), NotificationStatus.DONE, null);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.NUDGE;
    }
}
