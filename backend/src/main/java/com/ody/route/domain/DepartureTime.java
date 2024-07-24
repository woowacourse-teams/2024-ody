package com.ody.route.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DepartureTime {

    private static final long MINUTE_GAP = 10;

    private final LocalDateTime value;

    public DepartureTime(RouteTime routeTime, LocalDateTime meetingTime) {
        this.value = meetingTime.minusMinutes(routeTime.getMinutes() + MINUTE_GAP);
    }
}
