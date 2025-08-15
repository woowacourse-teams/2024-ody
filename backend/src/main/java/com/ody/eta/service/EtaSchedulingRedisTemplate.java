package com.ody.eta.service;

import com.ody.common.aop.EnableDeletedFilter;
import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@EnableDeletedFilter
public class EtaSchedulingRedisTemplate {

    private final long ttlMs;
    private final MateRepository mateRepository;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public EtaSchedulingRedisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            @Value("${spring.data.redis.ttl}") long ttlMs,
            MateRepository mateRepository
    ) {
        this.ttlMs = ttlMs;
        this.mateRepository = mateRepository;
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Transactional(readOnly = true)
    public void addAll(Meeting meeting) {
        mateRepository.findFetchedAllByMeetingId(meeting.getId())
                .forEach(mate -> add(EtaSchedulingKey.from(mate)));
    }

    public void add(EtaSchedulingKey etaSchedulingKey) {
        redisTemplate.opsForValue()
                .set(
                        etaSchedulingKey.serialize(),
                        LocalDateTime.now().toString(),
                        ttlMs,
                        TimeUnit.MILLISECONDS
                );
    }

    public void delete(EtaSchedulingKey etaSchedulingKey) {
        redisTemplate.delete(etaSchedulingKey.serialize());
    }

    public void deleteAll(List<EtaSchedulingKey> etaSchedulingKeys) {
        List<String> keys = etaSchedulingKeys.stream()
                .map(EtaSchedulingKey::serialize)
                .toList();
        redisTemplate.delete(keys);
    }
}
