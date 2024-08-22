package com.ody.route.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.exception.OdyBadRequestException;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.dto.OdsayResponse;
import java.util.Optional;
import java.util.OptionalLong;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OdsayResponseMapperTest {

    @DisplayName("소요시간을 반환한다")
    @Test
    void mapMinutesSuccess() {
        OdsayResponse validResponse = new OdsayResponse(
                Optional.empty(),
                Optional.empty(),
                OptionalLong.of(3L)
        );

        assertThat(OdsayResponseMapper.mapMinutes(validResponse))
                .isEqualTo(3L);
    }

    @DisplayName("출, 도착지가 700m 이내일 때 0분을 반환한다")
    @Test
    void mapZeroMinutesWhenCloseLocation() {
        OdsayResponse closeLocationResponse = new OdsayResponse(
                Optional.of("-98"),
                Optional.of("출,도착지가 700m 이내 입니다"),
                OptionalLong.empty()
        );

        assertThat(OdsayResponseMapper.mapMinutes(closeLocationResponse))
                .isEqualTo(0L);
    }

    @DisplayName("예외 응답 : 500 에러 코드는 OdyServerErrorException을 던진다")
    @Test
    void mapMinutes500Fail() {
        OdsayResponse serverExceptionResponse = new OdsayResponse(
                Optional.of("500"),
                Optional.of("서버 에러 발생"),
                OptionalLong.empty()
        );

        assertThatThrownBy(() -> OdsayResponseMapper.mapMinutes(serverExceptionResponse))
                .isInstanceOf(OdyServerErrorException.class);
    }

    @DisplayName("예외 응답 : 그 외 에러 코드는 OdyBadRequestException을 던진다")
    @Test
    void mapMinutes400Fail() {
        OdsayResponse badRequestExceptionException = new OdsayResponse(
                Optional.of("1"),
                Optional.of("그 외 에러 발생"),
                OptionalLong.empty()
        );

        assertThatThrownBy(() -> OdsayResponseMapper.mapMinutes(badRequestExceptionException))
                .isInstanceOf(OdyBadRequestException.class);
    }

    @DisplayName("response가 null인 경우 OdyServerErrorException을 던진다")
    @Test
    void nullCheck500Fail() {
        assertThatThrownBy(() -> OdsayResponseMapper.mapMinutes(null))
                .isInstanceOf(OdyServerErrorException.class);
    }

    @DisplayName("예외 응답 : 에러 코드와 소요시간이 모두 없는 경우 OdyServerException을 던진다")
    @Test
    void noneDataResponse500Fail() {
        OdsayResponse unValidResponse = new OdsayResponse(
                Optional.empty(),
                Optional.empty(),
                OptionalLong.empty()
        );
        assertThatThrownBy(() -> OdsayResponseMapper.mapMinutes(unValidResponse))
                .isInstanceOf(OdyServerErrorException.class);
    }
}
