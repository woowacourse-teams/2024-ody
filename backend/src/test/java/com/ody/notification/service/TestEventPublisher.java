package com.ody.notification.service;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public TestEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void transactionMethod(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void noneTransactionMethod(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }
}
