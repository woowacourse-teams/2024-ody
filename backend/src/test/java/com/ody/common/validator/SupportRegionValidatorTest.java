package com.ody.common.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.ody.common.annotation.SupportRegion;
import com.ody.eta.dto.request.MateEtaRequest;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class SupportRegionValidatorTest {

    private static SupportRegionValidator supportRegionValidator = new SupportRegionValidator();

    @BeforeAll
    static void setUp() {
        supportRegionValidator = new SupportRegionValidator();
        supportRegionValidator.initialize(new SupportRegionStub());
    }

    @DisplayName("수도권 내 위경도 좌표이면 true, 아니면 false를 반환한다.")
    @ParameterizedTest
    @MethodSource("supportRegionTestCases")
    void isValid(String latitude, String longitude, boolean expected) {
        MateEtaRequest mateEtaRequest = new MateEtaRequest(false, latitude, longitude);

        boolean valid = supportRegionValidator.isValid(mateEtaRequest, mock(ConstraintValidatorContext.class));

        assertThat(valid).isEqualTo(expected);
    }

    @DisplayName("행방불명 좌표(0.0)는 true를 반환한다")
    @Test
    void isValidMissingCoordinate() {
        MateEtaRequest mateEtaRequest = new MateEtaRequest(false, "0.0", "0.0");

        boolean valid = supportRegionValidator.isValid(mateEtaRequest, mock(ConstraintValidatorContext.class));

        assertThat(valid).isEqualTo(true);
    }

    private static Stream<Arguments> supportRegionTestCases() {
        return Stream.of(
                Arguments.of("37.505713", "127.050691", true), // 서울
                Arguments.of("37.694802", "126.318463", true), // 인천
                Arguments.of("37.291015", "127.724058", true), // 경기
                Arguments.of("36.747167", "127.418673", false), // 충북
                Arguments.of("38.099416", "127.979525", false), // 강원
                Arguments.of("39.031896", "125.769334", false) // 평양
        );
    }

    private static class SupportRegionStub implements SupportRegion {

        @Override
        public Class<? extends Annotation> annotationType() {
            return SupportRegion.class;
        }

        @Override
        public String latitudeFieldName() {
            return "currentLatitude";
        }

        @Override
        public String longitudeFieldName() {
            return "currentLongitude";
        }

        @Override
        public String message() {
            return "수도권 내 위경도 좌표만 가능합니다.";
        }

        @Override
        public Class<?>[] groups() {
            return new Class[0];
        }

        @Override
        public Class<? extends Payload>[] payload() {
            return new Class[0];
        }
    }
}
