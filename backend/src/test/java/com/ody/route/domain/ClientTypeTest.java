package com.ody.route.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.ody.meeting.domain.Coordinates;
import com.ody.route.service.GoogleRouteClient;
import com.ody.route.service.OdsayRouteClient;
import com.ody.route.service.RouteClient;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ClientTypeTest {

    @DisplayName("RouteClient 구현체 이름에 해당하는 ClientType를 반환한다.")
    @ParameterizedTest
    @MethodSource("getRouteClientAndClientType")
    void fromWithSuccess(RouteClient routeClient, ClientType expected) {
        ClientType actual = ClientType.from(routeClient);

        assertThat(actual).isEqualTo(expected);
    }

    public static Stream<Arguments> getRouteClientAndClientType() {
        return Stream.of(
                Arguments.of(mock(GoogleRouteClient.class), ClientType.GOOGLE),
                Arguments.of(mock(OdsayRouteClient.class), ClientType.ODSAY)
        );
    }

    @DisplayName("RouteClient 구현체 이름과 일치하는 타입이 없으면 예외가 발생한다.")
    @Test
    void fromWithException() {
        RouteClient routeClient = new Anonymous();

        assertThatThrownBy(() -> ClientType.from(routeClient))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static class Anonymous implements RouteClient {

        @Override
        public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
            return null;
        }
    }
}
