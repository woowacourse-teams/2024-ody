package com.ody.eta.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WebSocketEndpointTest {

    @DisplayName("subscribe endpoint 리스트를 반환한다.")
    @Test
    void getSubscribeEndpoints() {
        List<String> actual = WebSocketEndpoint.getSubscribeEndpoints();
        List<String> expected = List.of(
                WebSocketEndpoint.ETAS.getEndpoint(),
                WebSocketEndpoint.LOCATION.getEndpoint(),
                WebSocketEndpoint.DISCONNECT.getEndpoint(),
                WebSocketEndpoint.ERROR.getEndpoint()
        );

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("send endpoint 리스트를 반환한다.")
    @Test
    void getSendEndpoint() {
        List<String> actual = WebSocketEndpoint.getSendEndpoints();
        List<String> expected = List.of(
                WebSocketEndpoint.OPEN.getEndpoint(),
                WebSocketEndpoint.ETA_UPDATE.getEndpoint()
        );

        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
