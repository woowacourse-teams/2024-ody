package com.ody.route.domain;

import com.ody.route.service.RouteClient;
import java.util.Arrays;

public enum ClientType {

    ODSAY,
    GOOGLE,
    ;

    public static ClientType from(RouteClient routeClient) {
        String className = routeClient.getClass().getSimpleName().toUpperCase();
        return Arrays.stream(values())
                .filter(clientType -> className.contains(clientType.name()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 ClientType 입니다."));
    }
}
