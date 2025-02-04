package com.ody.eta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.eta.repository.EtaSchedulingRedisTemplate;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

class EtaSchedulingRedisTemplateTest extends BaseServiceTest {

    @Autowired
    private EtaSchedulingRedisTemplate etaSchedulingRedisTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @DisplayName("약속방 참여자 전원의 ETA 스케줄링 정보를 캐싱한다.")
    @Test
    void addAll() {
        Meeting meeting = fixtureGenerator.generateMeeting();
        Mate mate1 = fixtureGenerator.generateMate(meeting);
        Mate mate2 = fixtureGenerator.generateMate(meeting);

        EtaSchedulingKey expectedKey1 = EtaSchedulingKey.from(mate1);
        EtaSchedulingKey expectedKey2 = EtaSchedulingKey.from(mate2);

        etaSchedulingRedisTemplate.addAll(meeting);

        assertAll(
                () -> assertThat(getKey(expectedKey1)).isNotNull(),
                () -> assertThat(getKey(expectedKey2)).isNotNull()
        );
    }

    @DisplayName("약속방 참여자 1명의 ETA 스케줄링 정보를 캐싱한다.")
    @Test
    void add() {
        EtaSchedulingKey etaSchedulingKey = new EtaSchedulingKey("deviceToken", 1L, LocalDateTime.now());

        etaSchedulingRedisTemplate.add(etaSchedulingKey);

        String actual = getKey(etaSchedulingKey);
        assertThat(actual).isNotNull();
    }

    private String getKey(EtaSchedulingKey etaSchedulingKey) {
        return redisTemplate.opsForValue()
                .get(etaSchedulingKey.serialize());
    }
}
