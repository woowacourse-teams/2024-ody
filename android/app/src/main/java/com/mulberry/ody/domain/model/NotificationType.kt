package com.mulberry.ody.domain.model

import java.lang.IllegalArgumentException

sealed interface FCMType {
    fun isEtaType(): Boolean {
        return this == FCMNotificationType.ETA_NOTICE || this == FCMMessageType.ETA_SCHEDULING_NOTICE
    }

    companion object {
        fun from(type: String): FCMType =
            when (type) {
                "ENTRY" -> FCMNotificationType.ENTRY
                "DEPARTURE_REMINDER" -> FCMNotificationType.DEPARTURE_REMINDER
                "NUDGE" -> FCMNotificationType.NUDGE
                "ETA_NOTICE" -> FCMNotificationType.ETA_NOTICE
                "ETA_SCHEDULING_NOTICE" -> FCMMessageType.ETA_SCHEDULING_NOTICE
                else -> throw IllegalArgumentException("존재하지 않는 FCMType입니다. - $type")
            }
    }
}

enum class FCMNotificationType : FCMType {
    ENTRY,
    DEPARTURE_REMINDER,
    NUDGE,
    ETA_NOTICE,
}

enum class FCMMessageType : FCMType {
    ETA_SCHEDULING_NOTICE,
}
