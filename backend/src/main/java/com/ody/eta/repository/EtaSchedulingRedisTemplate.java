package com.ody.eta.repository;

import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EtaSchedulingRedisTemplate {

    private final long ttlMs;
    private final StringRedisTemplate redisTemplate;
    private final MateRepository mateRepository;

    public EtaSchedulingRedisTemplate(
            @Value("${spring.data.redis.ttl}") long ttlMs,
            StringRedisTemplate redisTemplate,
            MateRepository mateRepository
    ) {
        this.ttlMs = ttlMs;
        this.redisTemplate = redisTemplate;
        this.mateRepository = mateRepository;
    }

    public void addAll(Meeting meeting) {
        mateRepository.findFetchedAllByMeetingId(meeting.getId())
                .forEach(mate -> add(EtaSchedulingKey.from(mate)));
    }

    public void add(EtaSchedulingKey etaSchedulingKey) {
        redisTemplate.opsForValue()
                .set(etaSchedulingKey.serialize(), LocalDateTime.now().toString(), ttlMs, TimeUnit.MILLISECONDS);
    }

    public String get(EtaSchedulingKey etaSchedulingKey) {
        return redisTemplate.opsForValue()
                .get(etaSchedulingKey.serialize());
    }
}
