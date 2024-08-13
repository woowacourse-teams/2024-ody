package com.ody.notification.domain;

import com.ody.meeting.domain.Meeting;
import lombok.Getter;

@Getter
public class FcmTopic {

    private String value;

    public FcmTopic(String rawValue) {
        this.value = rawValue.replace(":", "-");
    }

    public FcmTopic(Meeting meeting) {
        this(build(meeting));
    }

    private static String build(Meeting meeting) {
        return new StringBuilder(meeting.getId().toString())
                .append("_")
                .append(meeting.getCreatedAt())
                .toString();
    }
}
