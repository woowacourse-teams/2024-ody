package com.ody.notification.domain.notice;

import com.ody.eta.domain.EtaSchedulingKey;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class EtaSchedulingNotice extends Notice {

    private final long meetingId;
    private final LocalDateTime meetingTime;

    public EtaSchedulingNotice(LocalDateTime sendAt, long meetingId, LocalDateTime meetingTime) {
        super(sendAt);
        this.meetingId = meetingId;
        this.meetingTime = meetingTime;
    }

    public EtaSchedulingNotice(LocalDateTime sendAt, EtaSchedulingKey key) {
        this(sendAt, key.meetingId(), key.meetingDateTime());
    }

    @Override
    public NoticeType getType() {
        return NoticeType.ETA_SCHEDULING_NOTICE;
    }
}
