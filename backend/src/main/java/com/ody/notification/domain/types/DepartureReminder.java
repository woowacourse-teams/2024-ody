package com.ody.notification.domain.types;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.FcmTopic;
import com.ody.notification.domain.NotificationStatus;
import com.ody.notification.domain.NotificationType;
import com.ody.route.domain.DepartureTime;

public class DepartureReminder extends AbstractNotification {

    public DepartureReminder(Mate mate, DepartureTime departureTime, FcmTopic fcmTopic) {
        super(mate, departureTime.getValue(), NotificationStatus.PENDING, fcmTopic);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.DEPARTURE_REMINDER;
    }
}
