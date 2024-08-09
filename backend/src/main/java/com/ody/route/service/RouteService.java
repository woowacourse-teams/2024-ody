package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.RouteTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteClient routeClient;

    public RouteTime calculateRouteTime(Location origin, Location target) {
        return routeClient.calculateRouteTime(origin, target);
    }
}
