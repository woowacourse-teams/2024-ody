package com.ody.notification.service.event;

import com.ody.notification.domain.Notification;
import com.ody.notification.domain.message.GroupMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushEvent extends ApplicationEvent {

    private final Notification notification;
    private final GroupMessage groupMessage;

    public PushEvent(Object source, Notification notification) {
        super(source);
        this.notification = notification;
        this.groupMessage = GroupMessage.from(notification);
    }
}
