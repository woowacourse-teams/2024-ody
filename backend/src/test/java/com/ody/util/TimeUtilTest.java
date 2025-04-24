package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @DisplayName("현재 시간보다 과거이면 true, 아니면 false를 반환한다")
    @ParameterizedTest
    @MethodSource("getTargetDateTimeAndIsPastResult")
    void isPast(LocalDateTime targetDateTime, boolean expected) {
        assertThat(TimeUtil.isPast(targetDateTime)).isEqualTo(expected);
    }

    private static Stream<Arguments> getTargetDateTimeAndIsPastResult() {
        LocalDateTime now = TimeUtil.nowWithTrim();

        return Stream.of(
                Arguments.of(now.minusHours(1), true),
                Arguments.of(now, false),
                Arguments.of(now.plusHours(1), false)
        );
    }

    @DisplayName("현재 시간보다 미래이면 true, 아니면 false를 반환한다")
    @ParameterizedTest
    @MethodSource("getTargetDateTimeAndIsUpcomingResult")
    void isUpcoming(LocalDateTime targetDateTime, boolean expected) {
        assertThat(TimeUtil.isUpcoming(targetDateTime)).isEqualTo(expected);
    }

    private static Stream<Arguments> getTargetDateTimeAndIsUpcomingResult() {
        LocalDateTime now = TimeUtil.nowWithTrim();

        return Stream.of(
                Arguments.of(now.minusHours(1), false),
                Arguments.of(now, false),
                Arguments.of(now.plusHours(1), true)
        );
    }
}
