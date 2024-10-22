package com.ody.route.mapper;

import com.ody.common.exception.OdyNotFoundException;
import com.ody.route.domain.ClientType;
import java.util.Arrays;

public enum RouteClientMapper {

    ODSAY("odsay", ClientType.ODSAY),
    GOOGLE("google", ClientType.GOOGLE),
    ;

    private String name;
    private ClientType type;

    RouteClientMapper(String name, ClientType type) {
        this.name = name;
        this.type = type;
    }

    public static ClientType from(String targetName) {
        return Arrays.stream(values())
                .filter(routeClient -> routeClient.name.equals(targetName.toLowerCase()))
                .findAny()
                .map(pathVariable -> pathVariable.type)
                .orElseThrow(() -> new OdyNotFoundException("조회할 수 없는 RouteClient입니다."));
    }
}
