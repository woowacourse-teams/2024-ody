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


    private final List<RouteClient> routeClients;

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        for (RouteClient client : routeClients) {
            try {
                return client.calculateRouteTime(origin, target);
            } catch (Exception e) {
                log.warn("Route Client 에러 : {} ", client.getClass().getSimpleName());
            }
        }
        throw new OdyServerErrorException("소요 시간 계산을 할 수 없습니다.");
    }
}
