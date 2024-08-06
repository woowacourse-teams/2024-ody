package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtaStatusTest {

    private LocalDateTime meetingTime;

    @BeforeEach
    void initMeetingTime() {
        meetingTime = LocalDateTime.now().withSecond(0).withNano(0);
    }

    @DisplayName("MISSING : 위치추적 불가여부가 true")
    @Test
    void returnMissing() {
        long countDownMinutes = 1L;
        boolean isArrived = false;
        boolean isMissing = true;

        assertThat(EtaStatus.from(countDownMinutes, meetingTime, isArrived, isMissing))
                .isEqualTo(EtaStatus.MISSING);
    }

    @DisplayName("LATE_WARNING : 현재시간 < 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLateWarning() {
        long countDownMinutes = 11L;
        LocalDateTime meetingTime10MinutesLater = meetingTime.plusMinutes(10L);
        boolean isArrived = false;
        boolean isMissing = false;

        assertThat(EtaStatus.from(countDownMinutes, meetingTime10MinutesLater, isArrived, isMissing))
                .isEqualTo(EtaStatus.LATE_WARNING);
    }

    @DisplayName("LATE : 현재시간 >= 약속시간  & 도착예정시간 > 약속시간")
    @Test
    void returnLate() {
        long countDownMinutes = 1L;
        boolean isArrived = false;
        boolean isMissing = false;

        assertThat(EtaStatus.from(countDownMinutes, meetingTime, isArrived, isMissing))
                .isEqualTo(EtaStatus.LATE);
    }

    @DisplayName("ARRIVED : isArrived == true")
    @Test
    void returnArrived() {
        long countDownMinutes = 1L;
        boolean isArrived = true;
        boolean isMissing = false;

        assertThat(EtaStatus.from(countDownMinutes, meetingTime, isArrived, isMissing))
                .isEqualTo(EtaStatus.ARRIVED);
    }

    @DisplayName("ARRIVAL_SOON: 현재시간 < 약속시간  & 도착예정시간 <= 약속시간")
    @Test
    void returnArrivalSoon() {
        long countDownMinutes = 10L;
        LocalDateTime meetingTime10MinutesLater = meetingTime.plusMinutes(10L);
        boolean isArrived = false;
        boolean isMissing = false;

        assertThat(EtaStatus.from(countDownMinutes, meetingTime10MinutesLater, isArrived, isMissing))
                .isEqualTo(EtaStatus.ARRIVAL_SOON);
    }
}
