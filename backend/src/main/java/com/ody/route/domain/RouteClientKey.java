package com.ody.route.domain;

import com.ody.route.service.RouteClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RouteClientKey {

    FAIL_KEY("route:fail:%s"),
    BLOCK_KEY("route:block:%s"),
    ;

    private final String value;

    public static String getFailKey(RouteClient routeClient) {
        return String.format(FAIL_KEY.value, routeClient.getClientType());
    }

    public static String getBlockKey(RouteClient routeClient) {
        return String.format(BLOCK_KEY.value, routeClient.getClientType());
    }
}
