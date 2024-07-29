package com.woowacourse.ody.data.remote.core.entity.notification.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.model.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class NotificationLogsResponse(
    @Json(name = "notiLog")
    val logList: List<NotificationLogResponse>,
) {
    fun toNotificationList(): List<NotificationLog> =
        logList.map {
            val type = NotificationType.from(it.type)
            val nickname = it.nickname
            val createdAt = it.createdAt.parseToLocalDateTime()
            NotificationLog(type, nickname, createdAt)
        }

    private fun String.parseToLocalDateTime(): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return LocalDateTime.parse(this, formatter)
    }
}
