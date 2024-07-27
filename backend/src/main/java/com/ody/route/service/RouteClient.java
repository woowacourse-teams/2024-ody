package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.RouteTime;

public interface RouteClient {

    RouteTime calculateRouteTime(Location origin, Location target);
}
