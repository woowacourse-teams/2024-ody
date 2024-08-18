package com.ody.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(

        @Schema(description = "토큰 인증 방식", example = "Bearer ")
        String tokenType,

        @Schema(description = "액세스 토큰", example = "ㅇㅇㅇㅇㅇ")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "ㅇㅇㅇㅇㅇ")
        String refreshToken
) {

    public AuthResponse() { // TODO: 제거
        this("Bearer ", "aaaa", "rrrr");
    }
}
