package com.ody.notification.domain;

import com.ody.meeting.domain.Meeting;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmTopic {

    private static final String TOPIC_NAME_DELIMITER = "_";
    private static final DateTimeFormatter MEETING_CREATE_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Column(name = "fcm_topic")
    private String value;

    public FcmTopic(Meeting meeting) {
        this(build(meeting));
    }

    public FcmTopic(String rawValue) {
        this.value = rawValue.replace(":", "-");
    }

    private static String build(Meeting meeting) {
        return meeting.getId().toString()
                + TOPIC_NAME_DELIMITER
                + meeting.getCreatedAt().format(MEETING_CREATE_AT_FORMAT);
    }
}
