package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.common.domain.BaseEntity;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaTest {

    @DisplayName("남은 시간을 카운트 다운 한다.")
    @Test
    void countDownMinutes() throws NoSuchFieldException, IllegalAccessException {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 10L);

        LocalDateTime now = LocalDateTime.now();
        setUpdatedAt(eta, now.minusMinutes(3L));

        assertThat(eta.countDownMinutes(now)).isEqualTo(eta.getRemainingMinutes() - 3L);
    }


    private void setUpdatedAt(Eta eta, LocalDateTime dateTime) throws NoSuchFieldException, IllegalAccessException {
        Field updatedAtField = BaseEntity.class.getDeclaredField("updatedAt");
        updatedAtField.setAccessible(true);
        updatedAtField.set(eta, dateTime);
    }
}
