package com.mulberry.ody.data.remote.core.entity.notification.mapper

import com.mulberry.ody.data.remote.core.entity.notification.response.NotificationLogsResponse
import com.mulberry.ody.domain.model.LogType
import com.mulberry.ody.domain.model.LogType.DEFAULT
import com.mulberry.ody.domain.model.LogType.DEPARTURE
import com.mulberry.ody.domain.model.LogType.DEPARTURE_REMINDER
import com.mulberry.ody.domain.model.LogType.ENTRY
import com.mulberry.ody.domain.model.LogType.MEMBER_DELETION
import com.mulberry.ody.domain.model.LogType.MEMBER_EXIT
import com.mulberry.ody.domain.model.LogType.NUDGE
import com.mulberry.ody.domain.model.NotificationLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogsResponse.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        NotificationLog(
            type = it.type.toLogType(),
            nickname = it.nickname,
            createdAt = it.createdAt.parseToLocalDateTime(),
            imageUrl = it.imageUrl,
        )
    }

private fun String.toLogType(): LogType {
    return when (this) {
        "ENTRY" -> ENTRY
        "DEPARTURE_REMINDER" -> DEPARTURE_REMINDER
        "DEPARTURE" -> DEPARTURE
        "MEMBER_DELETION" -> MEMBER_DELETION
        "MEMBER_EXIT" -> MEMBER_EXIT
        "NUDGE" -> NUDGE
        else -> DEFAULT
    }
}

private fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
