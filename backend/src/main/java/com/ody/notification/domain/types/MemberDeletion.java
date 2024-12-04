package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import java.time.LocalDateTime;

public class MemberDeletion extends AbstractNotification {

    private static final NotificationType type = NotificationType.MEMBER_DELETION;

    public MemberDeletion(Mate mate) {
        super(mate, type, LocalDateTime.now(), NotificationStatus.DONE, null);
    }
}
