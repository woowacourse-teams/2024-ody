package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

    @DisplayName("초/나노초가 제거된 현재시간을 반환한다")
    @Test
    void nowWithTrim() {
        LocalDateTime rawTime = LocalDateTime.now();
        LocalDateTime expectedTime = rawTime.withSecond(0).withNano(0);
        LocalDateTime actualTime = TimeUtil.nowWithTrim();

        assertAll(
                () -> assertThat(rawTime.isEqual(actualTime)).isFalse(),
                () -> assertThat(actualTime).isEqualTo(expectedTime)
        );
    }

    @DisplayName("초/나노초가 제거된 LocalTime을 반환한다")
    @Test
    void trimLocalTime() {
        LocalTime rawTime = LocalTime.now();
        LocalTime expectedTime = rawTime.withSecond(0).withNano(0);
        LocalTime actualTime = TimeUtil.trimSecondsAndNanos(rawTime);

        assertAll(
                () -> assertThat(rawTime.compareTo(actualTime)).isNotZero(),
                () -> assertThat(actualTime.compareTo(expectedTime)).isZero()
        );

    }

    @DisplayName("초/나노초가 제거된 LocalDateTime을 반환한다")
    @Test
    void trimLocalDateTime() {
        LocalDateTime rawTime = LocalDateTime.now();
        LocalDateTime expectedTime = rawTime.withSecond(0).withNano(0);
        LocalDateTime actualTime = TimeUtil.trimSecondsAndNanos(rawTime);

        assertAll(
                () -> assertThat(rawTime.isEqual(actualTime)).isFalse(),
                () -> assertThat(actualTime).isEqualTo(expectedTime)
        );
    }
}
