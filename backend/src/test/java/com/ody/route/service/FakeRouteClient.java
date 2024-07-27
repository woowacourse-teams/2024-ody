package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.RouteTime;

public class FakeRouteClient implements RouteClient {

    @Override
    public RouteTime calculateRouteTime(Location origin, Location target) {
        if (origin.getLatitude().equals(target.getLatitude()) && origin.getLongitude().equals(target.getLongitude())) {
            return RouteTime.ZERO;
        }
        return new RouteTime(16);
    }
}
