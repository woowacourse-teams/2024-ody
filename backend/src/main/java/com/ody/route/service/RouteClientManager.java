package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.domain.RouteClientKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteClientManager {
    private final List<RouteClient> routeClients;
    private final CircuitBreaker circuitBreaker;

    public List<RouteClient> getAvailableClients() {
        List<RouteClient> availableClients = routeClients.stream()
                .filter(this::isAvailable)
                .toList();

        if (availableClients.isEmpty()) {
            log.error("모든 RouteClient 차단되었습니다.");
            throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
        }
        return availableClients;
    }

    private boolean isAvailable(RouteClient routeClient) {
        return !circuitBreaker.isBlocked(RouteClientKey.getBlockKey(routeClient));
    }
}
