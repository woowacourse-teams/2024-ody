package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyServerErrorException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

class RouteClientManagerTest extends BaseServiceTest {

    @Autowired
    private RouteClientManager routeClientManager;

    @MockBean
    @Qualifier("odsay")
    private RouteClient odsayRouteClient;

    @MockBean
    @Qualifier("google")
    private RouteClient googleRouteClient;

    @DisplayName("이용 가능한 RouteClient를 반환한다.")
    @Test
    void getAvailableClients() {
        Mockito.when(routeClientCircuitBreaker.isBlocked(odsayRouteClient))
                .thenReturn(true);

        Mockito.when(routeClientCircuitBreaker.isBlocked(googleRouteClient))
                .thenReturn(false);

        List<RouteClient> availableClients = routeClientManager.getAvailableClients();

        assertThat(availableClients)
                .hasSize(1)
                .containsExactly(googleRouteClient);
    }

    @DisplayName("이용 가능한 RouteClient가 없으면 예외가 발생한다.")
    @Test
    void failWhenGetAvailableClientsBy() {
        Mockito.when(routeClientCircuitBreaker.isBlocked(odsayRouteClient))
                .thenReturn(true);

        Mockito.when(routeClientCircuitBreaker.isBlocked(googleRouteClient))
                .thenReturn(true);

        assertThatThrownBy(() -> routeClientManager.getAvailableClients())
                .isInstanceOf(OdyServerErrorException.class);
    }
}
