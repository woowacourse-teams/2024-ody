package com.ody.meeting.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LocationTest {

    @DisplayName("서울, 경기, 인천 지역으로만 Location을 생성할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"서울 강남구 테헤란로 411", "경기 성남시 분당구 서판교로 32", "인천 부평구 갈산로 2"})
    void createLocationSuccess(String address) {
        assertThatCode(() -> new Location(address, "123", "123"))
                .doesNotThrowAnyException();
    }

    @DisplayName("서울, 경기, 인천 지역이 아니면 Location을 생성시 예외가 발생한다.")
    @Test
    void createLocationException() {
        assertThatThrownBy(() -> new Location("경남 김해시 율하3로 76", "123", "123"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
