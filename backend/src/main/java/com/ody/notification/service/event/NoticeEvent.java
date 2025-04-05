package com.ody.notification.service.event;

import com.ody.notification.domain.message.AbstractMessage;
import com.ody.notification.domain.notice.Notice;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NoticeEvent<T extends AbstractMessage> extends ApplicationEvent {

    private final Notice notice;
    private final T message;

    public NoticeEvent(Object source, Notice notice, T message) {
        super(source);
        this.notice = notice;
        this.message = message;
    }
}
