package com.ody.eta.service;

import com.ody.eta.domain.EtaSchedulingKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisKeyExpiredListener extends KeyExpirationEventMessageListener {

    private final EtaSchedulingService etaSchedulingService;

    public RedisKeyExpiredListener(
            RedisMessageListenerContainer listenerContainer,
            EtaSchedulingService etaSchedulingService
    ) {
        super(listenerContainer);
        this.etaSchedulingService = etaSchedulingService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("TTL 만료 트리거 동작 - redis key : " + message.toString());
        EtaSchedulingKey etaSchedulingKey = EtaSchedulingKey.from(message.toString());
        etaSchedulingService.sendFallbackNotice(etaSchedulingKey);
    }
}
