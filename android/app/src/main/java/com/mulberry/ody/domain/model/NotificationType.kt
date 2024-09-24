package com.mulberry.ody.domain.model

enum class NotificationType {
    ENTRY,
    DEPARTURE_REMINDER,
    NUDGE,
    MEMBER_DELETION,
    ETA_NOTICE,
    DEFAULT,
    ;

    companion object {
        fun from(tag: String): NotificationType =
            when (tag) {
                "ENTRY" -> ENTRY
                "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
                "NUDGE" -> NUDGE
                "MEMBER_DELETION" -> MEMBER_DELETION
                "ETA_NOTICE" -> ETA_NOTICE
                else -> DEFAULT
            }
    }
}
