package com.ody.common;

import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteClient;

public class FakeOdsayRouteClient implements RouteClient {

    @Override
    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        if (origin.equals(target)) {
            return RouteTime.ZERO;
        }
        return new RouteTime(16);
    }
}
