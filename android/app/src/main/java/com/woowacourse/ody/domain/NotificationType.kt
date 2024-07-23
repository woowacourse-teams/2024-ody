package com.woowacourse.ody.domain

enum class NotificationType {
    ENTRY,
    DEPARTURE_REMINDER,
    DEPARTURE,
    DEFAULT,
    ;

    companion object {
        fun from(tag: String): NotificationType =
            when (tag) {
                "ENTRY" -> ENTRY
                "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
                "DEPARTURE" -> DEPARTURE
                else -> DEFAULT
            }
    }
}
