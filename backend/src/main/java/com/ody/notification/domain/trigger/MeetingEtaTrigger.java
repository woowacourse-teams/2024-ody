package com.ody.notification.domain.trigger;

import com.ody.eta.domain.EtaTriggerTime;
import com.ody.meeting.domain.Meeting;
import com.ody.notification.domain.FcmTopic;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MeetingEtaTrigger extends EtaTrigger {

    private final EtaTriggerTime triggerTime;
    private final FcmTopic fcmTopic;

    public static MeetingEtaTrigger from(Meeting meeting) {
        return new MeetingEtaTrigger(meeting.getId(),
                meeting.getMeetingTime(),
                new EtaTriggerTime(meeting.getMeetingTime()),
                new FcmTopic(meeting)
        );
    }

    private MeetingEtaTrigger(
            long meetingId,
            LocalDateTime meetingTime,
            EtaTriggerTime triggerTime,
            FcmTopic fcmTopic
    ) {
        super(meetingId, meetingTime);
        this.triggerTime = triggerTime;
        this.fcmTopic = fcmTopic;
    }
}
