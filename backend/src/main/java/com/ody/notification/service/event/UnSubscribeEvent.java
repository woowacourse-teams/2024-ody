package com.ody.notification.service.event;

import com.ody.member.domain.DeviceToken;
import com.ody.notification.domain.FcmTopic;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UnSubscribeEvent extends ApplicationEvent {

    private final DeviceToken deviceToken;
    private final FcmTopic topic;

    public UnSubscribeEvent(Object source, DeviceToken deviceToken, FcmTopic topic) {
        super(source);
        this.deviceToken = deviceToken;
        this.topic = topic;
    }
}
