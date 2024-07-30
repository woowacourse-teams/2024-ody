package com.ody.common;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.RouteTime;
import com.ody.route.service.RouteClient;

public class FakeRouteClient implements RouteClient {

    @Override
    public RouteTime calculateRouteTime(Location origin, Location target) {
        if (origin.getLatitude().equals(target.getLatitude()) && origin.getLongitude().equals(target.getLongitude())) {
            return RouteTime.ZERO;
        }
        return new RouteTime(16);
    }
}
