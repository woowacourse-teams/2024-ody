package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.route.domain.DepartureTime;

public class DepartureReminder extends AbstractNotification {

    private static final NotificationType type = NotificationType.DEPARTURE_REMINDER;

    public DepartureReminder(Mate mate, DepartureTime departureTime, FcmTopic fcmTopic) {
        super(mate, type, departureTime.getValue(), NotificationStatus.PENDING, fcmTopic);
    }
}
