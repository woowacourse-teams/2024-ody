package com.ody.route.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ApiCallTest {

    private static final ApiCall NULL_STATE_API_CALL = new ApiCall(1L, ClientType.ODSAY, 10, LocalDate.now(), null);

    @DisplayName("처음 state를 업데이트하면 false이다.")
    @Test
    void updateStateFirstTime() {
        NULL_STATE_API_CALL.updateState();

        assertThat(NULL_STATE_API_CALL.getState()).isFalse();
    }

    @DisplayName("state가 null일 때 isState는 true이다.")
    @Test
    void isStateWhenNullState() {
        assertThat(NULL_STATE_API_CALL.isState()).isTrue();
    }

    @DisplayName("state가 null이 아니면 isState는 state이다.")
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void isStateWhenNotNull(boolean state) {
        ApiCall apiCall = new ApiCall(1L, ClientType.ODSAY, 10, LocalDate.now(), state);

        assertThat(apiCall.isState()).isEqualTo(state);
    }
}
