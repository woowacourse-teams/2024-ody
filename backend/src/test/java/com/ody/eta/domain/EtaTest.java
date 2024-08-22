package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import com.ody.util.TimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaTest {

    @DisplayName("Eta의 소요시간이 수정되었다면 true를 반환한다.")
    @Test
    void isModified() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        LocalDateTime now = TimeUtil.nowWithTrim().minusMinutes(1);
        Eta eta = new Eta(mate, 1L, now, now);

        eta.updateRemainingMinutes(5L);

        assertThat(eta.isModified()).isTrue();
    }

    @DisplayName("Eta의 소요시간이 수정되었지 않았다면 false 반환한다.")
    @Test
    void isNotModified() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 1L);

        assertThat(eta.isModified()).isFalse();
    }

    @DisplayName("지각할 예정이라면 true를 반환한다.")
    @Test
    void willBeLate() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 1L);

        assertThat(eta.isArrivalSoon(odyMeeting)).isFalse();
    }

    @DisplayName("지각할 예정이 아니라면 false를 반환한다.")
    @Test
    void wontLate() {
        Meeting twoMinutesLaterMeeting = new Meeting(
                "오디",
                LocalDate.now(),
                LocalTime.now().plusMinutes(2L),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(twoMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 1L);

        assertThat(eta.isArrivalSoon(twoMinutesLaterMeeting)).isTrue();
    }

    @DisplayName("남은 시간을 카운트 다운 한다.")
    @Test
    void countDownMinutes() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        LocalDateTime now = TimeUtil.nowWithTrim();
        Eta eta = new Eta(1L, mate, 10L, false, false, now, now.minusMinutes(3L));

        assertThat(eta.countDownMinutes()).isEqualTo(eta.getRemainingMinutes() - 3L);
    }
}
