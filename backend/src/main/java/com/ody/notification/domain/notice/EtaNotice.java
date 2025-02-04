package com.ody.notification.domain.notice;

import com.ody.meeting.domain.Meeting;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class EtaNotice extends Notice {

    private final String meetingName;
    private final long meetingId;

    public EtaNotice(LocalDateTime sendAt, String meetingName, long meetingId) {
        super(sendAt);
        this.meetingName = meetingName;
        this.meetingId = meetingId;
    }

    public EtaNotice(LocalDateTime sendAt, Meeting meeting) {
        this(sendAt, meeting.getName(), meeting.getId());
    }

    @Override
    public NoticeType getType() {
        return NoticeType.ETA_NOTICE;
    }
}
