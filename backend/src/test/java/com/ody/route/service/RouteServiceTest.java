package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ody.common.BaseServiceTest;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.RouteTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

class RouteServiceTest extends BaseServiceTest {

    @MockBean
    @Qualifier("odsay")
    private RouteClient odsayRouteClient;

    @MockBean
    @Qualifier("google")
    private RouteClient googleRouteClient;

    @SpyBean
    private ApiCallService apiCallService;

    @Autowired
    private RouteService routeService;

    private Coordinates origin;
    private Coordinates target;

    @BeforeEach
    void setUp() {
        origin = new Coordinates("37.505419", "127.050817");
        target = new Coordinates("37.515253", "127.102895");

        when(odsayRouteClient.getClientType()).thenReturn(ClientType.ODSAY);
        when(googleRouteClient.getClientType()).thenReturn(ClientType.GOOGLE);
    }

    @DisplayName("OdsayRouteClient에 에러가 발생하지 않으면 첫 번째 외부 API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByOdsayRouteClient() {
        when(odsayRouteClient.calculateRouteTime(origin, target)).thenReturn(new RouteTime(16));
        when(googleRouteClient.calculateRouteTime(origin, target)).thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(origin, target).getMinutes();
        RouteTime expectRouteTime = new RouteTime(16);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> verify(odsayRouteClient, times(1)).calculateRouteTime(origin, target),
                () -> Mockito.verifyNoInteractions(googleRouteClient),
                () -> verify(apiCallService, times(1)).increaseCountByClientType(odsayRouteClient.getClientType()),
                () -> verify(apiCallService, Mockito.never()).increaseCountByClientType(
                        googleRouteClient.getClientType())

        );
    }

    @DisplayName("OdsayRouteClient에 에러가 발생하면 그 다음 요소인 Google API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByGoogleRouteClient() {
        origin = new Coordinates("37.505419", "127.050817");
        target = new Coordinates("37.515253", "127.102895");

        when(odsayRouteClient.calculateRouteTime(origin, target))
                .thenThrow(new RuntimeException("Odsay API 에러 발생"));
        when(googleRouteClient.calculateRouteTime(origin, target)).thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(origin, target).getMinutes();
        RouteTime expectRouteTime = new RouteTime(18);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> verify(odsayRouteClient, times(1)).calculateRouteTime(origin, target),
                () -> verify(googleRouteClient, times(1)).calculateRouteTime(origin, target),
                () -> verify(apiCallService, Mockito.never()).increaseCountByClientType(
                        odsayRouteClient.getClientType()),
                () -> verify(apiCallService, times(1)).increaseCountByClientType(googleRouteClient.getClientType())
        );
    }

    @DisplayName("OdsayRouteClient를 비활성화하면 Odsay API를 사용해 소요시간을 반환하지 않는다.")
    @Test
    void disableOdsayApiCall() {
        when(googleRouteClient.calculateRouteTime(origin, target)).thenReturn(new RouteTime(18));

        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());

        routeService.calculateRouteTime(origin, target);
        assertAll(
                () -> verify(odsayRouteClient, times(0)).calculateRouteTime(any(), any()),
                () -> verify(googleRouteClient, times(1)).calculateRouteTime(any(), any())
        );
    }

    @DisplayName("OdsayRouteClient를 재활성화하면 Odsay API를 사용해 소요시간을 반환한다.")
    @Test
    void enableOdsayApiCall() {
        RouteTime odsayRouteTime = new RouteTime(16);
        when(odsayRouteClient.calculateRouteTime(origin, target)).thenReturn(odsayRouteTime);
        when(googleRouteClient.calculateRouteTime(origin, target)).thenReturn(new RouteTime(18));

        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());
        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());

        routeService.calculateRouteTime(origin, target);
        assertAll(
                () -> verify(odsayRouteClient, times(1)).calculateRouteTime(any(), any()),
                () -> verify(googleRouteClient, times(0)).calculateRouteTime(any(), any())
        );
    }
}
