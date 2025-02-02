package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private static final long CLOSEST_LOCATION_DURATION = 10L;

    private final RouteClientManager routeClientManager;
    private final ApiCallService apiCallService;

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        List<RouteClient> availableClients = routeClientManager.getAvailableClients();
        for (RouteClient routeClient : availableClients) {
            try {
                RouteTime routeTime = calculateTime(routeClient, origin, target);
                apiCallService.increaseCountByClientType(routeClient.getClientType());
                log.info(
                        "{} API 소요 시간 계산 : {}분, mateId : {}",
                        routeClient.getClientType(),
                        routeTime.getMinutes(),
                        MDC.get("mateId")
                );
                return routeTime;
            } catch (OdyServerErrorException exception) {
                log.warn("{} API 에러 발생 :  ", routeClient.getClientType(), exception);
            }
        }
        log.error("모든 RouteClient API 사용 불가");
        throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
    }

    private RouteTime calculateTime(RouteClient routeClient, Coordinates origin, Coordinates target) {
        RouteTime calculatedRouteTime = routeClient.calculateRouteTime(origin, target);
        if (calculatedRouteTime.equals(RouteTime.CLOSEST_EXCEPTION_TIME)) {
            return new RouteTime(CLOSEST_LOCATION_DURATION);
        }
        return calculatedRouteTime;
    }
}
