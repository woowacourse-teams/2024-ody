package com.ody.auth.dto.response;

public record AppleAuthValidationResponse(
        String accessToken,
        String tokenType,
        String expiresIn,
        String refreshToken,
        String idToken
) {

}
