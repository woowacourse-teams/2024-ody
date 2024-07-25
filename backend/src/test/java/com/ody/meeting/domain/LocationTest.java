package com.ody.meeting.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyBadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LocationTest {

    @DisplayName("서울, 경기, 인천 지역으로만 Location을 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"서울 강남구 테헤란로 411", "경기 성남시 분당구 서판교로 32", "인천 부평구 갈산로 2"})
    void createLocationSuccess(String address) {
        assertThatCode(() -> new Location(address, "0", "0"))
                .doesNotThrowAnyException();
    }

    @DisplayName("서울, 경기, 인천 지역이 아니면 Location을 생성시 예외가 발생한다.")
    @Test
    void createLocationException() {
        assertThatThrownBy(() -> new Location("경남 김해시 율하3로 76", "0", "0"))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("위도는 -90부터 90 범위, 소수점 자리수 6 이하이며 소수점 및 부호를 포함할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"-90", "90", "-90.000000"})
    void createWithValidLatitude(String latitude) {
        assertThatCode(() -> new Location("서울 강남구 테헤란로 411", latitude, "0"))
                .doesNotThrowAnyException();
    }

    @DisplayName("위도는 -90부터 90 범위, 소수점 자리수 6 이하가 아니면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-90.000001", "90.000001", "-90.0000001", "abc", ""})
    void createWithInvalidLatitude(String latitude) {
        assertThatThrownBy(() -> new Location("서울 강남구 테헤란로 411", latitude, "0"))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("경도는 -180부터 180 범위, 소수점 자리수 6 이하이며 소수점 및 부호를 포함할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"-180", "180", "-180.000"})
    void createWithValidLongitude(String longitude) {
        assertThatCode(() -> new Location("서울 강남구 테헤란로 411", "0", longitude))
                .doesNotThrowAnyException();
    }

    @DisplayName("경도는 -180부터 180 범위, 소수점 자리수 6 이하가 아니면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-180.000001", "180.000001", "-180.0000001", "abc", ""})
    void createWithInvalidLongitude(String longitude) {
        assertThatThrownBy(() -> new Location("서울 강남구 테헤란로 411", "0", longitude))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
