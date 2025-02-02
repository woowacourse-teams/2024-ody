package com.ody.eta.repository;

import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.repository.MateRepository;
import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EtaSchedulingRedisTemplate extends StringRedisTemplate {

    private final long ttlMs;
    private final MateRepository mateRepository;

    @Autowired
    public EtaSchedulingRedisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            @Value("${spring.data.redis.ttl}") long ttlMs,
            MateRepository mateRepository
    ) {
        super(redisConnectionFactory);
        this.ttlMs = ttlMs;
        this.mateRepository = mateRepository;
    }

    public void addAll(Meeting meeting) {
        mateRepository.findFetchedAllByMeetingId(meeting.getId())
                .forEach(mate -> add(EtaSchedulingKey.from(mate)));
    }

    public void add(EtaSchedulingKey etaSchedulingKey) {
        opsForValue().set(
                etaSchedulingKey.serialize(),
                LocalDateTime.now().toString(),
                ttlMs,
                TimeUnit.MILLISECONDS
        );
    }

    public String get(EtaSchedulingKey etaSchedulingKey) {
        return opsForValue().get(etaSchedulingKey.serialize());
    }
}
