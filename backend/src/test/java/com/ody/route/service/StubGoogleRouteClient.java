package com.ody.route.service;

import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.ClientType;
import com.ody.route.domain.RouteTime;

public class StubGoogleRouteClient implements RouteClient {

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        if (origin.equals(target)) {
            return RouteTime.ZERO;
        }
        return new RouteTime(18);
    }

    @Override
    public ClientType getClientType() {
        return ClientType.GOOGLE;
    }
}
