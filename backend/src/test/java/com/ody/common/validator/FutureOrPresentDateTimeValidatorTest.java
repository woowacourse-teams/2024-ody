package com.ody.common.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.ody.common.annotation.FutureOrPresentDateTime;
import com.ody.meeting.dto.request.MeetingSaveRequest;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FutureOrPresentDateTimeValidatorTest {

    private FutureOrPresentDateTimeValidator futureOrPresentDateTimeValidator;

    @BeforeEach
    void init() {
        futureOrPresentDateTimeValidator = new FutureOrPresentDateTimeValidator();
        futureOrPresentDateTimeValidator.initialize(new FutureOrPresentDateTime() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return FutureOrPresentDateTime.class;
            }

            @Override
            public String dateFieldName() {
                return "date";
            }

            @Override
            public String timeFieldName() {
                return "time";
            }

            @Override
            public String message() {
                return "날짜와 시간은 현재 이후여야 합니다.";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }
        });
    }

    @DisplayName("현재 날짜와 시간 이후면 true, 아니면 false를 반환한다.")
    @ParameterizedTest
    @MethodSource("dateTimeTestCases")
    void isValid(LocalDate date, LocalTime time, boolean expected) {
        MeetingSaveRequest meetingSaveRequest = new MeetingSaveRequest(
                "우테코 16조",
                date,
                time,
                "서울 송파구 올림픽로35다길 42",
                "37.515298",
                "127.103113",
                "오디",
                "서울 강남구 테헤란로 411",
                "37.505713",
                "127.050691"
        );

        boolean valid = futureOrPresentDateTimeValidator.isValid(
                meetingSaveRequest,
                mock(ConstraintValidatorContext.class)
        );

        assertThat(valid).isEqualTo(expected);
    }

    private static Stream<Arguments> dateTimeTestCases() {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        return Stream.of(
                Arguments.of(today, currentTime, false),
                Arguments.of(today, currentTime.plusHours(1), true),
                Arguments.of(today, currentTime.minusHours(1), false),
                Arguments.of(today.plusDays(1), currentTime, true)
        );
    }
}
