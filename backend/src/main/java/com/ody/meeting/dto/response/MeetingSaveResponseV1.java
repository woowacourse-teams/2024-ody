package com.ody.meeting.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;

public record MeetingSaveResponseV1(

        @Schema(description = "모임 ID", example = "1")
        Long id,

        @Schema(description = "모임 이름", example = "우테코 16조")
        String name,

        @Schema(description = "모임 날짜", type = "string", example = "2024-07-15")
        LocalDate date,

        @Schema(description = "모임 시간", type = "string", example = "14:00")
        LocalTime time,

        @Schema(description = "도착지 주소", example = "서울 송파구 올림픽로35다길 42")
        String targetAddress,

        @Schema(description = "도착지 위도", example = "37.515298")
        String targetLatitude,

        @Schema(description = "도착지 경도", example = "127.103113")
        String targetLongitude,

        @Schema(description = "초대코드", example = "초대코드")
        String inviteCode
) {

}
