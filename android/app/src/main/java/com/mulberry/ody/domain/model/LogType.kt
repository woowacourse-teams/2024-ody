package com.mulberry.ody.domain.model

enum class LogType {
    ENTRY,
    DEPARTURE_REMINDER,
    DEPARTURE,
    NUDGE,
    MEMBER_DELETION,
    MEMBER_EXIT,
    DEFAULT,
    ;

    companion object {
        fun from(tag: String): LogType =
            when (tag) {
                "ENTRY" -> ENTRY
                "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
                "DEPARTURE" -> DEPARTURE
                "MEMBER_DELETION" -> MEMBER_DELETION
                "MEMBER_EXIT" -> MEMBER_EXIT
                "NUDGE" -> NUDGE
                else -> DEFAULT
            }
    }
}
