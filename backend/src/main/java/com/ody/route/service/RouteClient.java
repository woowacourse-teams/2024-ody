package com.ody.route.service;

import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;

public interface RouteClient {

    RouteTime calculateRouteTime(Coordinates origin, Coordinates target);
}
