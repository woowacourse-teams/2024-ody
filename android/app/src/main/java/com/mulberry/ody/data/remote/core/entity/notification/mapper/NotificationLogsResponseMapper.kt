package com.mulberry.ody.data.remote.core.entity.notification.mapper

import com.mulberry.ody.data.remote.core.entity.notification.response.NotificationLogsResponse
import com.mulberry.ody.domain.model.LogType
import com.mulberry.ody.domain.model.NotificationLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogsResponse.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        NotificationLog(
            type = LogType.from(it.type),
            nickname = it.nickname,
            createdAt = it.createdAt.parseToLocalDateTime(),
            imageUrl = it.imageUrl,
        )
    }

private fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
