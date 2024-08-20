package com.ody.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthorizationHeaderTest {

    @DisplayName("")
    @Test
    void some() {
        AuthorizationHeader authorizationHeader = new AuthorizationHeader(
                "Bearer access-token=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzI0MDcxMDk0fQ.jurbZe7A987FIJKvUjF0I0plwUFakOcFqRP73TmpxDc refresh-token=eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MjQwNzEwOTR9.8a-CCA_z9_K3yHnc1i3hRgZR_X5xJWy_1tvkXd99JbE"
        );

        assertThat(authorizationHeader.getAccessToken().getValue()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzI0MDcxMDk0fQ.jurbZe7A987FIJKvUjF0I0plwUFakOcFqRP73TmpxDc");
        assertThat(authorizationHeader.getRefreshToken().getValue()).isEqualTo("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MjQwNzEwOTR9.8a-CCA_z9_K3yHnc1i3hRgZR_X5xJWy_1tvkXd99JbE");

        // when

        // then

    }

}
