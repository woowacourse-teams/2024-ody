package com.ody.route.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DepartureTime {

    private static final long MINUTE_GAP = 10;

    private final LocalDateTime value;

    public DepartureTime(Duration duration, LocalDateTime meetingTime) {
        this.value = meetingTime.minusMinutes(duration.getMinutes() + MINUTE_GAP);
    }
}
