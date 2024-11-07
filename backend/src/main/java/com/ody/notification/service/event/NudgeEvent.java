package com.ody.notification.service.event;

import com.ody.mate.domain.Mate;
import com.ody.notification.domain.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NudgeEvent extends ApplicationEvent {

    private final Mate requestMate;
    private final Notification nudgeNotification;

    public NudgeEvent(Object source, Mate requestMate, Notification nudgeNotification) {
        super(source);
        this.requestMate = requestMate;
        this.nudgeNotification = nudgeNotification;
    }
}
