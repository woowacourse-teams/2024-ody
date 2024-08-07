package com.ody.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DistanceCalculatorTest {

    private static final int NAVER_CALCULATED_DISTANCE = 1300;

    @DisplayName("계산된 직선 거리가 네이버 지도에서 실제 계산된 거리와 오차가 50미터 이내이다.")
    @Test
    void calculateDistance() {
        double originLatitude = 37.501144;
        double originLongitude = 127.037226;
        double targetLatitude = 37.505407;
        double targetLongitude = 127.050900;

        double distance = DistanceCalculator.calculate(
                originLatitude,
                originLongitude,
                targetLatitude,
                targetLongitude
        );

        assertAll(
                () -> assertThat(distance).isGreaterThanOrEqualTo(NAVER_CALCULATED_DISTANCE - 50),
                () -> assertThat(distance).isLessThanOrEqualTo(NAVER_CALCULATED_DISTANCE + 50)
        );
    }
}
