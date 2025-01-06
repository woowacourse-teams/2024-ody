package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.route.domain.ApiCall;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteClientManager {

    private final List<RouteClient> routeClients;
    private final ApiCallService apiCallService;

    public List<RouteClient> getAvailableClients() {
        List<RouteClient> availableClients = routeClients.stream()
                .filter(this::isEnabled)
                .toList();

        if (availableClients.isEmpty()) {
            log.error("모든 RouteClient 차단되었습니다.");
            throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
        }
        return availableClients;
    }

    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void initializeClientApiCalls() {
        routeClients.stream()
                .map(client -> new ApiCall(client.getClientType()))
                .forEach(apiCallService::save);
    }

    private boolean isEnabled(RouteClient routeClient) {
        return Boolean.TRUE.equals(apiCallService.getEnabledByClientType(routeClient.getClientType()));
    }
}
