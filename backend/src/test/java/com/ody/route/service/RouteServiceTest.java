package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.ody.common.BaseServiceTest;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

class RouteServiceTest extends BaseServiceTest {

    @MockBean
    @Qualifier("odsay")
    private RouteClient odsayRouteClient;

    @MockBean
    @Qualifier("google")
    private RouteClient googleRouteClient;

    @MockBean
    private ApiCallService apiCallService;

    @Autowired
    private RouteService routeService;

    @DisplayName("OdsayRouteClient에 에러가 발생하지 않으면 첫 번째 외부 API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByOdsayRouteClient() {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        Mockito.when(odsayRouteClient.calculateRouteTime(origin, target))
                .thenReturn(new RouteTime(16));

        Mockito.when(googleRouteClient.calculateRouteTime(origin, target))
                .thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(origin, target).getMinutes();
        RouteTime expectRouteTime = new RouteTime(16);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> Mockito.verify(odsayRouteClient, Mockito.times(1)).calculateRouteTime(origin, target),
                () -> Mockito.verifyNoInteractions(googleRouteClient),
                () -> Mockito.verify(apiCallService, Mockito.times(1)).increaseCountByRouteClient(odsayRouteClient),
                () -> Mockito.verify(apiCallService, Mockito.never()).increaseCountByRouteClient(googleRouteClient)

        );
    }

    @DisplayName("OdsayRouteClient에 에러가 발생하면 그 다음 요소인 Google API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByGoogleRouteClient() {
        Coordinates origin = new Coordinates("37.505419", "127.050817");
        Coordinates target = new Coordinates("37.515253", "127.102895");

        Mockito.when(odsayRouteClient.calculateRouteTime(origin, target))
                .thenThrow(new RuntimeException("Odsay API 에러 발생"));

        Mockito.when(googleRouteClient.calculateRouteTime(origin, target))
                .thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(origin, target).getMinutes();
        RouteTime expectRouteTime = new RouteTime(18);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> Mockito.verify(odsayRouteClient, Mockito.times(1)).calculateRouteTime(origin, target),
                () -> Mockito.verify(googleRouteClient, Mockito.times(1)).calculateRouteTime(origin, target),
                () -> Mockito.verify(apiCallService, Mockito.never()).increaseCountByRouteClient(odsayRouteClient),
                () -> Mockito.verify(apiCallService, Mockito.times(1)).increaseCountByRouteClient(googleRouteClient)
        );
    }
}
