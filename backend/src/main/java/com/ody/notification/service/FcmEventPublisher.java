package com.ody.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(ApplicationEvent applicationEvent) {
        eventPublisher.publishEvent(applicationEvent);
    }

    @Transactional
    public void publishWithTransaction(ApplicationEvent applicationEvent) {
        eventPublisher.publishEvent(applicationEvent);
    }
}
