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

    private final List<RouteClient> routeClients;
    private final ApiCallService apiCallService;

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        for (RouteClient client : routeClients) {
            if (isDisabled(client)) {
                log.info("{} API 사용이 비활성화되어 건너뜁니다.", client.getClass().getSimpleName());
                continue;
            }

            try {

                RouteTime routeTime = calculateTime(client, origin, target);
                apiCallService.increaseCountByClientType(client.getClientType());
                log.info(
                        "mateId : {}, {} API 사용한 소요 시간 계산 : {}분",
                        MDC.get("mateId"),
                        client.getClientType(),
                        routeTime.getMinutes()
                );
                return routeTime;
            } catch (Exception exception) {
                log.warn("Route Client 에러 : {} ", client.getClass().getSimpleName(), exception);
            }
        }
        log.error("모든 소요시간 계산 API 사용 불가");
        throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
    }

    private RouteTime calculateTime(RouteClient client, Coordinates origin, Coordinates target) {
        RouteTime calculatedRouteTime = client.calculateRouteTime(origin, target);
        if (calculatedRouteTime.equals(RouteTime.CLOSEST_EXCEPTION_TIME)) {
            return new RouteTime(CLOSEST_LOCATION_DURATION);
        }
        return calculatedRouteTime;
    }

    private boolean isDisabled(RouteClient client) {
        return Boolean.FALSE.equals(apiCallService.getEnabledByClientType(client.getClientType()));
    }
}
