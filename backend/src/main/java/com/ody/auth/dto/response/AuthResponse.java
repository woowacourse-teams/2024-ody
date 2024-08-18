package com.ody.auth.dto.response;

import com.ody.auth.token.AccessToken;
import com.ody.auth.token.RefreshToken;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(

        @Schema(description = "토큰 인증 방식", example = "Bearer ")
        String tokenType,

        @Schema(description = "액세스 토큰", example = "access-token")
        String accessToken,

        @Schema(description = "리프레시 토큰", example = "refresh-token")
        String refreshToken
) {

    public static final String TOKEN_TYPE = "Bearer ";

    public AuthResponse(AccessToken accessToken, RefreshToken refreshToken) {
        this(TOKEN_TYPE, accessToken.getValue(), refreshToken.getValue());
    }
}
