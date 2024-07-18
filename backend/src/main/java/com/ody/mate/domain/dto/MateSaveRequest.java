package com.ody.mate.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MateSaveRequest(

        @Schema(description = "초대코드", example = "초대코드")
        String inviteCode,

        @Schema(description = "참여자 닉네임", example = "제리")
        String nickname,

        @Schema(description = "출발지 주소", example = "서울 강남구 테헤란로 411")
        String originAddress,

        @Schema(description = "출발지 위도", example = "37.505713")
        String originLatitude,

        @Schema(description = "출발지 경도", example = "127.050691")
        String originLongitude
) {

}
