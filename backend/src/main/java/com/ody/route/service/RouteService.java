package com.ody.route.service;

import com.ody.common.exception.OdyServerErrorException;
import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.RouteTime;
import com.ody.route.dto.ApiCallToggleResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouteService {

    private final List<RouteClient> routeClients;
    private final ApiCallService apiCallService;
    private final Map<ClientType, Boolean> routeClientsEnabled;

    public RouteService(List<RouteClient> routeClients, ApiCallService apiCallService) {
        this.routeClients = routeClients;
        this.apiCallService = apiCallService;
        this.routeClientsEnabled = routeClients.stream()
                .collect(Collectors.toMap(RouteClient::getClientType, routeClient -> true));
    }

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        for (RouteClient client : routeClients) {
            if (isDisabled(client)) {
                log.info("{} API 사용이 비활성화되어 건너뜁니다.", client.getClass().getSimpleName());
                continue;
            }

            try {
                RouteTime routeTime = client.calculateRouteTime(origin, target);
                apiCallService.increaseCountByRouteClient(client);
                log.info("{}를 사용한 소요 시간 계산 성공", client.getClass().getSimpleName());
                return routeTime;
            } catch (Exception exception) {
                log.warn("Route Client 에러 : {} ", client.getClass().getSimpleName(), exception);
            }
        }
        log.error("모든 소요시간 계산 API 사용 불가");
        throw new OdyServerErrorException("서버에 장애가 발생했습니다.");
    }

    private boolean isDisabled(RouteClient client) {
        return Boolean.FALSE.equals(routeClientsEnabled.getOrDefault(client.getClientType(), true));
    }

    public ApiCallToggleResponse toggleOdsayApiCall() {
        return toggleApiCall(ClientType.ODSAY);
    }

    public ApiCallToggleResponse toggleGoogleApiCall() {
        return toggleApiCall(ClientType.GOOGLE);
    }

    private ApiCallToggleResponse toggleApiCall(ClientType clientType) {
        boolean previousEnabled = routeClientsEnabled.get(clientType);
        routeClientsEnabled.put(clientType, !previousEnabled);
        return new ApiCallToggleResponse(routeClientsEnabled.get(clientType));
    }
}
