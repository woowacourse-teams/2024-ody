package com.ody.notification.service.event;

import com.ody.notification.domain.message.GroupMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoticeEvent extends ApplicationEvent {

    private final GroupMessage groupMessage;

    public NoticeEvent(Object source, GroupMessage groupMessage) {
        super(source);
        this.groupMessage = groupMessage;
    }
}
