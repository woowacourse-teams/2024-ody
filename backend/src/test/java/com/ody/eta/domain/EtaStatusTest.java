package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ody.common.Fixture;
import com.ody.mate.domain.Mate;
import com.ody.mate.domain.Nickname;
import com.ody.meeting.domain.Meeting;
import com.ody.member.domain.Member;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaStatusTest {

    private Mate mate;
    private LocalDateTime meetingTime;
    private LocalDateTime now;

    @BeforeEach
    void initMeetingTime() {
        Meeting odyMeeting = Fixture.ODY_MEETING;
        Member member1 = Fixture.MEMBER1;
        mate = new Mate(odyMeeting, member1, new Nickname("콜리"), Fixture.ORIGIN_LOCATION, 10L);

        meetingTime = LocalDateTime.now();
        now = LocalDateTime.now();
    }

    @DisplayName("MISSING : 위치추적 불가여부가 true")
    @Test
    void returnMissing() {
        Eta eta = new Eta(mate, 5L);
        boolean isMissing = true;

        assertThat(EtaStatus.from(mate.getNicknameValue(), eta, meetingTime, now, isMissing))
                .isEqualTo(EtaStatus.MISSING);
    }

    @DisplayName("LATE_WARNING : 현재시간 < 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLateWarning() {
        Eta eta = new Eta(mate, 11L);
        LocalDateTime tenMinutesLater = meetingTime.plusMinutes(1L);
        boolean isMissing = false;

        assertThat(EtaStatus.from(mate.getNicknameValue(), eta, tenMinutesLater, now, isMissing))
                .isEqualTo(EtaStatus.LATE_WARNING);
    }

    @DisplayName("LATE : 현재시간 >= 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLate() {
        Eta eta = new Eta(mate, 10L);
        boolean isMissing = false;

        assertThat(EtaStatus.from(mate.getNicknameValue(), eta, meetingTime, now, isMissing))
                .isEqualTo(EtaStatus.LATE);
    }

    @DisplayName("ARRIVED : isArrived == true")
    @Test
    void returnArrived() {
        Eta eta = new Eta(mate, 10L);
        boolean isMissing = false;

        eta.updateArrived();

        assertThat(EtaStatus.from(mate.getNicknameValue(), eta, meetingTime, now, isMissing))
                .isEqualTo(EtaStatus.ARRIVED);
    }

    @DisplayName("ARRIVAL_SOON: 현재시간 < 약속시간  & 도착예정시간 <= 약속시간")
    @Test
    void returnArrivalSoon() {
        LocalDateTime oneMinutesLater = meetingTime.plusMinutes(1L);
        Eta eta = new Eta(mate, 0L);
        boolean isMissing = false;

        assertThat(EtaStatus.from(mate.getNicknameValue(), eta, oneMinutesLater, now, isMissing))
                .isEqualTo(EtaStatus.ARRIVAL_SOON);
    }
}
