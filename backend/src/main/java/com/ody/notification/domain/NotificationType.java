package com.ody.notification.domain;

public enum NotificationType {

    ENTRY,
    DEPARTURE_REMINDER,
    NUDGE,
    LEAVE,
    MEMBER_DELETION,
    ETA_NOTICE,
    ETA_SCHEDULING_NOTICE,
    ;

    public boolean isDepartureReminder() {
        return this == DEPARTURE_REMINDER;
    }
}
