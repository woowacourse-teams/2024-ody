package com.ody.auth.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppleAuthValidationResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("Apple JSON 응답을 AppleAuthValidationResponse로 매핑한다.")
    @Test
    void shouldMapJsonToAppleAuthValidationResponse() throws Exception {
        String response = """
                {
                  "access_token": "adg61...67Or9",
                  "token_type": "Bearer",
                  "expires_in": 3600,
                  "refresh_token": "rca7...lABoQ",
                  "id_token": "eyJra...96sZg"
                }
                """;

        AppleAuthValidationResponse result = objectMapper.readValue(response, AppleAuthValidationResponse.class);

        assertThat(result.refreshToken()).isEqualTo("rca7...lABoQ");
    }
}
