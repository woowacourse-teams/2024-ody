package com.ody.meetinglog.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MeetingLogType {

    ENTRY_LOG("ENTRY"),
    NUDGE_LOG("NUDGE"),
    LEAVE_LOG("LEAVE"),
    MEMBER_DELETION_LOG("MEMBER_DELETION"),
    DEPARTURE_REMINDER("DEPARTURE_REMINDER"),
    ;

    private final String name;
}
