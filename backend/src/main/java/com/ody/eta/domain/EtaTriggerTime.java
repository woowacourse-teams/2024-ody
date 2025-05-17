package com.ody.eta.domain;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class EtaTriggerTime {

    private static final long MEETING_ETA_TRIGGER_DIFFER_MINUTES = 30L;

    private final LocalDateTime time;

    public EtaTriggerTime(LocalDateTime meetingTime) {
        this.time = meetingTime.minusMinutes(MEETING_ETA_TRIGGER_DIFFER_MINUTES);
    }
}
