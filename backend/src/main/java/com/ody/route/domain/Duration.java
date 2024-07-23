package com.ody.route.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Duration {

    public static final Duration ZERO = new Duration(0);

    private final long minutes;
}
