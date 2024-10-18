package com.ody.route.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ApiCallTest {

    @DisplayName("처음 enabled를 업데이트하면 false이다.")
    @Test
    void updateEnabledFirstTime() {
        ApiCall apiCall = new ApiCall(1L, ClientType.ODSAY, 10, LocalDate.now(), null);

        apiCall.updateEnabled();

        assertThat(apiCall.getEnabled()).isFalse();
    }

    @DisplayName("enabled가 null일 때 isEnabled는 true이다.")
    @Test
    void isEnabledWhenNullEnabled() {
        ApiCall apiCall = new ApiCall(1L, ClientType.ODSAY, 10, LocalDate.now(), null);

        assertThat(apiCall.getEnabled()).isTrue();
    }

    @DisplayName("enabled가 null이 아니면 isEnabled는 enabled이다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isEnabledWhenNotNull(boolean enabled) {
        ApiCall apiCall = new ApiCall(1L, ClientType.ODSAY, 10, LocalDate.now(), enabled);

        assertThat(apiCall.getEnabled()).isEqualTo(enabled);
    }
}
