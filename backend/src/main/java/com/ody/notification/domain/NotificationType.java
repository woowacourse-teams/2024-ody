package com.ody.notification.domain;

public enum NotificationType {

    ENTRY,
    DEPARTURE_REMINDER,
    NUDGE,
    MEMBER_DELETION,
    ;

    public boolean isDepartureReminder() {
        return this == DEPARTURE_REMINDER;
    }
}
