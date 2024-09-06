package com.ydo.ody.data.remote.core.entity.notification.mapper

import com.ydo.ody.data.remote.core.entity.notification.response.NotificationLogsResponse
import com.ydo.ody.domain.model.NotificationLog
import com.ydo.ody.domain.model.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogsResponse.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        val type = NotificationType.from(it.type)
        val nickname = it.nickname
        val createdAt = it.createdAt.parseToLocalDateTime()
        val imageUrl = it.imageUrl

        NotificationLog(type, nickname, createdAt, imageUrl)
    }

private fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
