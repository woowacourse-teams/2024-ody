package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.RouteTime;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteClient routeClient;

    public DepartureTime calculateDepartureTime(Location origin, Location target, LocalDateTime meetingTime) {
        RouteTime routeTime = routeClient.calculateRouteTime(origin, target);
        return new DepartureTime(routeTime, meetingTime);
    }
}
