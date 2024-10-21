package com.ody.route.domain;

import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RouteTime {

    public static final RouteTime ZERO = new RouteTime(0L);

    private final long minutes;

    public boolean isSame(long minutes) {
        return this.minutes == minutes;
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
