package com.mulberry.ody.data.remote.core.entity.notification.mapper

import com.mulberry.ody.data.remote.core.entity.notification.response.NotificationLogsResponse
import com.mulberry.ody.domain.model.NotificationLog
import com.mulberry.ody.domain.model.NotificationLogType
import com.mulberry.ody.domain.model.NotificationLogType.DEFAULT
import com.mulberry.ody.domain.model.NotificationLogType.DEPARTURE
import com.mulberry.ody.domain.model.NotificationLogType.DEPARTURE_REMINDER
import com.mulberry.ody.domain.model.NotificationLogType.ENTRY
import com.mulberry.ody.domain.model.NotificationLogType.MEMBER_DELETION
import com.mulberry.ody.domain.model.NotificationLogType.MEMBER_EXIT
import com.mulberry.ody.domain.model.NotificationLogType.NUDGE
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogsResponse.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        NotificationLog(
            type = it.type.toNotificationLogType(),
            nickname = it.nickname,
            createdAt = it.createdAt.parseToLocalDateTime(),
            imageUrl = it.imageUrl,
        )
    }

private fun String.toNotificationLogType(): NotificationLogType {
    return when (this) {
        "ENTRY" -> ENTRY
        "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
        "DEPARTURE" -> DEPARTURE
        "MEMBER_DELETION" -> MEMBER_DELETION
        "LEAVE" -> MEMBER_EXIT
        "NUDGE" -> NUDGE
        else -> DEFAULT
    }
}

private fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
