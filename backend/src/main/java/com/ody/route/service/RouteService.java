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
                RouteTime routeTime = client.calculateRouteTime(origin, target);
                log.info("{}를 사용한 소요 시간 계산 성공", client.getClass().getSimpleName());
                return routeTime;
            } catch (Exception exception) {
                log.warn("Route Client 에러 : {} ", client.getClass().getSimpleName(), exception);
            }
        }
        log.error("모든 소요시간 계산 API 사용 불가");
        throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
    }
}
