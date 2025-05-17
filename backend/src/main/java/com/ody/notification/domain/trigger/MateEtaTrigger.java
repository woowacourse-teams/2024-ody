package com.ody.notification.domain.trigger;

import com.ody.eta.domain.EtaSchedulingKey;
import com.ody.util.TimeUtil;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MateEtaTrigger extends EtaTrigger {

    private final LocalDateTime triggerTime;
    private final String deviceToken;

    public static MateEtaTrigger from(EtaSchedulingKey key) {
        return new MateEtaTrigger(
                key.meetingId(),
                key.meetingDateTime(),
                TimeUtil.nowWithTrim(),
                key.deviceToken()
        );
    }

    private MateEtaTrigger(
            long meetingId,
            LocalDateTime meetingTime,
            LocalDateTime triggerTime,
            String deviceToken
    ) {
        super(meetingId, meetingTime);
        this.triggerTime = triggerTime;
        this.deviceToken = deviceToken;
    }
}
