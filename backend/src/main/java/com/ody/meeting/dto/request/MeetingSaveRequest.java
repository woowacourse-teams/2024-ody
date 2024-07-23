package com.ody.meeting.dto.request;

import com.ody.common.annotation.FutureOrPresentDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@FutureOrPresentDateTime(dateFieldName = "date", timeFieldName = "time")
public record MeetingSaveRequest(

        @Schema(description = "모임 이름", example = "우테코 16조")
        @Size(min = 1, max = 15, message = "약속 이름은 1글자 이상, 16자 미만으로 입력 가능합니다.")
        String name,

        @Schema(description = "모임 날짜", example = "2024-07-15")
        LocalDate date,

        @Schema(description = "모임 시간", example = "14:00")
        LocalTime time,

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        String targetAddress,

        @Schema(description = "도착지 위도", example = "37.515298")
        String targetLatitude,

        @Schema(description = "도착지 경도", example = "127.103113")
        String targetLongitude,

        @Schema(description = "모임장 닉네임", example = "오디")
        @Size(min = 1, max = 9, message = "닉네임은 1글자 이상, 10자 미만으로 입력 가능합니다.")
        String nickname,

        @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
        String originAddress,

        @Schema(description = "출발지 위도", example = "37.505713")
        String originLatitude,

        @Schema(description = "출발지 경도", example = "127.050691")
        String originLongitude
) {

}
