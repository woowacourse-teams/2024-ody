package com.ody.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AppleAuthValidationResponse(

        String accessToken,
        String tokenType,
        int expiresIn,
        String refreshToken,
        String idToken
) {

}
