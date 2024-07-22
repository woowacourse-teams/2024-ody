package com.ody.route.service;

import com.ody.meeting.domain.Location;
import com.ody.route.domain.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteClient routeClient;

    public Duration calcualteDuration(Location origin, Location target) {
        return routeClient.calculateDuration(origin, target); //TODO: LocalDateTime으로 반환하기
    }
}
