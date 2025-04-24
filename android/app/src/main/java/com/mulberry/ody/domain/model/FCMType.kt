package com.mulberry.ody.domain.model

import java.lang.IllegalArgumentException

sealed interface FCMType {
    companion object {
        fun from(type: String): FCMType =
            when (type) {
                "ENTRY" -> NotificationType.ENTRY
                "DEPARTURE_REMINDER" -> NotificationType.DEPARTURE_REMINDER
                "NUDGE" -> NotificationType.NUDGE
                "ETA_NOTICE" -> NotificationType.ETA_NOTICE
                "ETA_SCHEDULING_NOTICE" -> MessageType.ETA_SCHEDULING_NOTICE
                else -> throw IllegalArgumentException("존재하지 않는 FCMType입니다. - $type")
            }
    }
}

enum class NotificationType : FCMType {
    ENTRY,
    DEPARTURE_REMINDER,
    NUDGE,
    ETA_NOTICE,
}

enum class MessageType : FCMType {
    ETA_SCHEDULING_NOTICE,
}
