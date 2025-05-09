package com.ody.route.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ody.common.BaseServiceTest;
import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;

class RouteServiceTest extends BaseServiceTest {

    private static final Coordinates ORIGIN = new Coordinates("37.505419", "127.050817");
    private static final Coordinates TARGET = new Coordinates("37.515253", "127.102895");


    @SpyBean
    @Qualifier("odsay")
    private RouteClient odsayRouteClient;

    @SpyBean
    @Qualifier("google")
    private RouteClient googleRouteClient;

    @SpyBean
    private ApiCallService apiCallService;

    @Autowired
    private RouteService routeService;

    @DisplayName("OdsayRouteClient에 에러가 발생하지 않으면 첫 번째 외부 API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByOdsayRouteClient() {
        when(odsayRouteClient.calculateRouteTime(ORIGIN, TARGET)).thenReturn(new RouteTime(16));
        when(googleRouteClient.calculateRouteTime(ORIGIN, TARGET)).thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(ORIGIN, TARGET).getMinutes();
        RouteTime expectRouteTime = new RouteTime(16);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> Mockito.verify(odsayRouteClient, times(1)).calculateRouteTime(ORIGIN, TARGET),
                () -> verify(googleRouteClient, Mockito.never()).calculateRouteTime(ORIGIN, TARGET),
                () -> Mockito.verify(apiCallService, times(1))
                        .increaseCountByClientType(odsayRouteClient.getClientType()),
                () -> Mockito.verify(apiCallService, Mockito.never()).increaseCountByClientType(
                        googleRouteClient.getClientType())
        );
    }

    @DisplayName("OdsayRouteClient에서 700m 이내라 소요시간 -1을 반환하면 10분으로 소요시간이 전환된다")
    @Test
    void calculateClosestDurationRouteTimeByOdsayRouteClient() {
        Mockito.when(odsayRouteClient.calculateRouteTime(ORIGIN, TARGET))
                .thenReturn(new RouteTime(-1));

        Mockito.when(googleRouteClient.calculateRouteTime(ORIGIN, TARGET))
                .thenReturn(new RouteTime(18));

        RouteTime result = routeService.calculateRouteTime(ORIGIN, TARGET);
        RouteTime expectRouteTime = new RouteTime(10);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime),
                () -> Mockito.verify(odsayRouteClient, Mockito.times(1)).calculateRouteTime(ORIGIN, TARGET),
                () -> Mockito.verify(googleRouteClient, Mockito.never()).calculateRouteTime(ORIGIN, TARGET),
                () -> Mockito.verify(apiCallService, Mockito.times(1))
                        .increaseCountByClientType(odsayRouteClient.getClientType()),
                () -> Mockito.verify(apiCallService, Mockito.never())
                        .increaseCountByClientType(googleRouteClient.getClientType())
        );
    }

    @DisplayName("OdsayRouteClient에 에러가 발생하면 그 다음 요소인 Google API를 사용해 소요시간을 반환한다.")
    @Test
    void calculateRouteTimeByGoogleRouteClient() {
        Mockito.when(odsayRouteClient.calculateRouteTime(ORIGIN, TARGET))
                .thenThrow(new OdyServerErrorException("Odsay API 에러 발생"));

        Mockito.when(googleRouteClient.calculateRouteTime(ORIGIN, TARGET))
                .thenReturn(new RouteTime(18));

        long result = routeService.calculateRouteTime(ORIGIN, TARGET).getMinutes();
        RouteTime expectRouteTime = new RouteTime(18);

        assertAll(
                () -> assertThat(result).isEqualTo(expectRouteTime.getMinutes()),
                () -> verify(googleRouteClient, times(1)).calculateRouteTime(ORIGIN, TARGET),
                () -> verify(apiCallService, times(1)).increaseCountByClientType(googleRouteClient.getClientType())
        );
    }

    @DisplayName("OdsayRouteClient를 비활성화하면 Odsay API를 사용해 소요시간을 반환하지 않는다.")
    @Test
    void disableOdsayApiCall() {
        when(googleRouteClient.calculateRouteTime(ORIGIN, TARGET)).thenReturn(new RouteTime(18));

        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());

        routeService.calculateRouteTime(ORIGIN, TARGET);
        assertAll(
                () -> verify(odsayRouteClient, times(0)).calculateRouteTime(any(), any()),
                () -> verify(googleRouteClient, times(1)).calculateRouteTime(any(), any())
        );
    }

    @DisplayName("OdsayRouteClient를 재활성화하면 Odsay API를 사용해 소요시간을 반환한다.")
    @Test
    void enableOdsayApiCall() {
        RouteTime odsayRouteTime = new RouteTime(16);
        when(odsayRouteClient.calculateRouteTime(ORIGIN, TARGET)).thenReturn(odsayRouteTime);
        when(googleRouteClient.calculateRouteTime(ORIGIN, TARGET)).thenReturn(new RouteTime(18));

        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());
        apiCallService.toggleApiCallEnabled(odsayRouteClient.getClientType());

        routeService.calculateRouteTime(ORIGIN, TARGET);
        assertAll(
                () -> verify(odsayRouteClient, times(1)).calculateRouteTime(any(), any()),
                () -> verify(googleRouteClient, times(0)).calculateRouteTime(any(), any())
        );
    }
}
