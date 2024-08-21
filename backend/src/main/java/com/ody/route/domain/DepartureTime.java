package com.ody.route.domain;

import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class DepartureTime {

    private static final long MINUTE_GAP = 10;

    private final LocalDateTime value;

    public DepartureTime(Meeting meeting, long estimatedMinutes) {
        LocalDateTime meetingTime = LocalDateTime.of(meeting.getDate(), meeting.getTime());
        this.value = meetingTime.minusMinutes(estimatedMinutes + MINUTE_GAP);
    }

    public boolean isBefore(LocalDateTime target) {
        return value.isBefore(target);
    }
}
