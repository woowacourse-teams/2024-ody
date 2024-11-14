package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private static final long CLOSEST_LOCATION_DURATION = 10L;

    private final RouteClientManager routeClientManager;
    private final ApiCallService apiCallService;
    private final RouteClientCircuitBreaker routeClientCircuitBreaker;

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        List<RouteClient> availableClients = routeClientManager.getAvailableClients();
        for (RouteClient routeClient : availableClients) {
            if (isDisabled(routeClient)) {
                log.info("{} API 사용이 차단되어 건너뜁니다.", routeClient.getClass().getSimpleName());
                continue;
            }
            try {
                RouteTime routeTime = calculateTime(routeClient, origin, target);
                apiCallService.increaseCountByClientType(routeClient.getClientType());
                log.info("{} API를 사용한 소요 시간 계산 성공", routeClient.getClass().getSimpleName());
                return routeTime;
            } catch (Exception exception) {
                log.warn("{} API 에러 발생 :  ", routeClient.getClass().getSimpleName(), exception);
                routeClientCircuitBreaker.recordFailCountInMinutes(routeClient);
                routeClientCircuitBreaker.determineBlock(routeClient);
            }
        }
        log.error("모든 RouteClient API 사용 불가");
        throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
    }

    private boolean isDisabled(RouteClient routeClient) {
        return Boolean.FALSE.equals(apiCallService.getEnabledByClientType(routeClient.getClientType()));
    }

    private RouteTime calculateTime(RouteClient routeClient, Coordinates origin, Coordinates target) {
        RouteTime calculatedRouteTime = routeClient.calculateRouteTime(origin, target);
        if (calculatedRouteTime.equals(RouteTime.CLOSEST_EXCEPTION_TIME)) {
            return new RouteTime(CLOSEST_LOCATION_DURATION);
        }
        return calculatedRouteTime;
    }
}
