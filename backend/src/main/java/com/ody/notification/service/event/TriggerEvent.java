package com.ody.notification.service.event;

import com.ody.notification.domain.message.AbstractMessage;
import com.ody.notification.domain.trigger.EtaTrigger;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TriggerEvent<T extends AbstractMessage> extends ApplicationEvent {

    private final EtaTrigger trigger;
    private final T message;

    public TriggerEvent(Object source, EtaTrigger trigger, T message) {
        super(source);
        this.trigger = trigger;
        this.message = message;
    }
}
