package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaStatusTest {

    @DisplayName("MISSING : 위치추적 불가여부가 true")
    @Test
    void returnMissing() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 10L);
        eta.updateMissingBy(true);

        assertThat(EtaStatus.of(eta, mate.getMeeting()))
                .isEqualTo(EtaStatus.MISSING);
    }

    @DisplayName("LATE_WARNING : 현재시간 < 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLateWarning() {
        Meeting oneMinutesLaterMeeting = new Meeting(
                "오디",
                LocalDate.now(),
                LocalTime.now().plusMinutes(1L),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(oneMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 2L);

        assertThat(EtaStatus.of(eta, mate.getMeeting()))
                .isEqualTo(EtaStatus.LATE_WARNING);
    }

    @DisplayName("LATE : 현재시간 >= 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLate() {
        Meeting oneMinutesBeforeMeeting = new Meeting(
                "오디",
                LocalDate.now(),
                LocalTime.now().minusMinutes(1L),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(oneMinutesBeforeMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 1L);

        assertThat(EtaStatus.of(eta, mate.getMeeting()))
                .isEqualTo(EtaStatus.LATE);
    }

    @DisplayName("ARRIVED : isArrived == true")
    @Test
    void returnArrived() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 10L);

        eta.updateArrived();

        assertThat(EtaStatus.of(eta, mate.getMeeting()))
                .isEqualTo(EtaStatus.ARRIVED);
    }

    @DisplayName("ARRIVAL_SOON: 현재시간 < 약속시간  & 도착예정시간 <= 약속시간")
    @Test
    void returnArrivalSoon() {
        Meeting oneMinutesLaterMeeting = new Meeting(
                "오디",
                LocalDate.now(),
                LocalTime.now().plusMinutes(2L),
                Fixture.TARGET_LOCATION,
                "초대코드"
        );
        Member member1 = Fixture.MEMBER1;
        Mate mate = new Mate(oneMinutesLaterMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);
        Eta eta = new Eta(mate, 0L);

        assertThat(EtaStatus.of(eta, oneMinutesLaterMeeting))
                .isEqualTo(EtaStatus.ARRIVAL_SOON);
    }
}
