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
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaSchedulingKeyTest {

    @DisplayName("EtaSchedulingRedisTemplate key를 생성한다.")
    @Test
    void generateKey() {
        // given
        DeviceToken myDeviceToken = new DeviceToken("myDeviceToken");
        Long id1 = 1L;
        LocalDateTime now = TimeUtil.nowWithTrim();

        Member member = new Member(Fixture.PID1, Fixture.NICKNAME1, Fixture.IMAGE_URL1, myDeviceToken);
        Meeting meeting = new Meeting(
                id1,
                Fixture.SUSHI_MEETING_NAME,
                now.toLocalDate(),
                now.toLocalTime(),
                Fixture.TARGET_LOCATION,
                Fixture.INVITE_CODE,
                false
        );
        Mate mate = new Mate(meeting, member, Fixture.NICKNAME1, Fixture.ORIGIN_LOCATION, Fixture.ESTIMATED_MINUTES_60);

        // when
        String actualKey = EtaSchedulingKey.from(mate).serialize();

        // then
        String expectedKey = myDeviceToken.getValue() + "/"
                + id1 + "/"
                + now;

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
