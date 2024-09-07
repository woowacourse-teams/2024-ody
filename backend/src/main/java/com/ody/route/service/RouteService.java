package com.ody.route.service;

import com.ody.meeting.domain.Coordinates;
import com.ody.route.domain.RouteTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteClient routeClient;

    public RouteTime calculateRouteTime(Coordinates origin, Coordinates target) {
        return routeClient.calculateRouteTime(origin, target);
    }
}
