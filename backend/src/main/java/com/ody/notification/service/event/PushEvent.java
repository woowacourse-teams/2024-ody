package com.ody.notification.service.event;

import com.google.firebase.messaging.Message;
import com.ody.notification.domain.Notification;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushEvent extends ApplicationEvent {

    private final Notification notification;

    public PushEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
    }
}
