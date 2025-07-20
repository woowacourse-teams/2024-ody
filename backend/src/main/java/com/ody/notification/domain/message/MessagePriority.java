package com.ody.notification.domain.message;

import com.google.firebase.messaging.AndroidConfig.Priority;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessagePriority {

    HIGH(Priority.HIGH, "10"),
    NORMAL(Priority.NORMAL, "5"),
    ;

    private final Priority androidPriority;
    private final String iosPriority;

    public boolean isHigh() {
        return this == HIGH;
    }
}
