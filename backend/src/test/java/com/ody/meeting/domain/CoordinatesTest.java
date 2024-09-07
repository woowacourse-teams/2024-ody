package com.ody.meeting.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyBadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CoordinatesTest {

    @DisplayName("double로 변환 불가능한 좌표면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "1a2.456"})
    void generateNonConvertibleCoordinatesThrowException(String rawCoordinate) {
        assertThatThrownBy(() -> new Coordinates(rawCoordinate, "1.234"))
                .isInstanceOf(OdyBadRequestException.class);
    }
}
