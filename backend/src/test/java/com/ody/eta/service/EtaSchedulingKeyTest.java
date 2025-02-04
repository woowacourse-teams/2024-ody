package com.ody.eta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.Fixture;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.mate.domain.Mate;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.DeviceToken;
import com.ody.member.domain.Member;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaSchedulingKeyTest {

    @DisplayName("EtaSchedulingRedisTemplate key를 생성한다.")
    @Test
    void generateKey() {
        // given
        DeviceToken deviceToken = new DeviceToken("device_token");
        LocalDateTime meetingDateTime = LocalDateTime.now();

        Member member = Fixture.Member(1L, deviceToken);
        Meeting meeting = Fixture.Meeting(2L, meetingDateTime);
        Mate mate = Fixture.Mate(3L, meeting, member);

        // when
        String actualKey = EtaSchedulingKey.from(mate).serialize();

        // then
        String expectedKey = deviceToken.getValue() + "/"
                + meeting.getId().toString() + "/"
                + meetingDateTime;

        assertThat(actualKey).isEqualTo(expectedKey);
    }

    @DisplayName("EtaSchedulingKey 구분자 형식이 맞지 않으면 예외가 발생한다.")
    @Test
    void generateKeyFail() {
        String invalidKey = "device_token$2$2025-01-28T19:03:53.736094";

        assertThatThrownBy(() -> EtaSchedulingKey.from(invalidKey))
                .isInstanceOf(OdyServerErrorException.class)
                .hasMessage("유효하지 않은 EtaSchedulingKey 입니다.");
    }
}
