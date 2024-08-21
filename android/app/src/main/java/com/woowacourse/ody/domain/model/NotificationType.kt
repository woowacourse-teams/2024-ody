package com.woowacourse.ody.domain.model

enum class NotificationType {
    ENTRY,
    DEPARTURE_REMINDER,
    DEPARTURE,
    NUDGE,
    DEFAULT,
    ;

    companion object {
        fun from(tag: String): NotificationType =
            when (tag) {
                "ENTRY" -> ENTRY
                "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
                "DEPARTURE" -> DEPARTURE
                "NUDGE" -> NUDGE
                else -> DEFAULT
            }
    }
}
