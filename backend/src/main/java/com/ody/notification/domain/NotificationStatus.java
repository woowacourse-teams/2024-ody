package com.ody.notification.domain;

public enum NotificationStatus {

    PENDING,
    DONE,
    DISMISSED,
    ;

    public boolean isDismissed() {
        return this == DISMISSED;
    }
}
