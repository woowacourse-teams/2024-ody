package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.route.domain.DepartureTime;
import java.time.LocalDateTime;

public class DepartureReminder extends AbstractNotification {

    private static final NotificationType type = NotificationType.DEPARTURE_REMINDER;

    public DepartureReminder(Mate mate, DepartureTime departureTime, FcmTopic fcmTopic) {
        super(mate, type, calculateSendAt(departureTime), NotificationStatus.PENDING, fcmTopic);
    }

    private static LocalDateTime calculateSendAt(DepartureTime departureTime) {
        if (departureTime.isBefore(LocalDateTime.now())) {
            return LocalDateTime.now();
        }
        return departureTime.getValue();
    }
}
