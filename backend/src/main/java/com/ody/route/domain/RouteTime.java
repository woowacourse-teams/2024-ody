package com.ody.route.domain;

import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RouteTime {

    public static final RouteTime ZERO = new RouteTime(0L);

    private final long minutes;

    public boolean isZero() {
        return this.equals(ZERO);
    }

    public RouteTime addMinutes(long minutes) {
        return new RouteTime(minutes + this.minutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RouteTime routeTime = (RouteTime) o;
        return getMinutes() == routeTime.getMinutes();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMinutes());
    }
}
