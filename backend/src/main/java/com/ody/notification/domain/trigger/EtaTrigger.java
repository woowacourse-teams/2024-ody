package com.ody.notification.domain.trigger;

import com.ody.notification.domain.message.MessagePriority;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class EtaTrigger {

    private final long meetingId;
    private final LocalDateTime meetingTime;

    public final TriggerType getType() {
        return TriggerType.ETA_SCHEDULING;
    }

    public abstract MessagePriority getPriority();
}
