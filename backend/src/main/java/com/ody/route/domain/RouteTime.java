package com.ody.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RouteTime {

    public static final RouteTime ZERO = new RouteTime(0L);

    private final long minutes;
}
