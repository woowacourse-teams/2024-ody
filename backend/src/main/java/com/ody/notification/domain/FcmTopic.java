package com.ody.notification.domain;

import com.ody.meeting.domain.Meeting;
import lombok.Getter;

@Getter
public class FcmTopic {

    private String value;

    public FcmTopic(Meeting meeting) {
        this(build(meeting));
    }
    
    public FcmTopic(String rawValue) {
        this.value = rawValue.replace(":", "-");
    }

    private static String build(Meeting meeting) {
        return meeting.getId().toString()
                + "_"
                + meeting.getCreatedAt();
    }
}
