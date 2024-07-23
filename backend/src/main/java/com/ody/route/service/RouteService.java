package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.DepartureTime;
import com.ody.route.domain.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteClient routeClient;

    public DepartureTime calculateDepartureTime(Location origin, Location target, LocalDateTime meetingTime) {
        Duration duration = routeClient.calculateDuration(origin, target);
        return new DepartureTime(duration, meetingTime);
    }
}
