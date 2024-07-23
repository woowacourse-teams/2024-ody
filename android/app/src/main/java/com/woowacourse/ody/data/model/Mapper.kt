package com.woowacourse.ody.data.model

import com.woowacourse.ody.data.model.notification.NotificationLogEntities
import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogEntities.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        val type = NotificationType.from(it.type)
        val nickname = it.nickname
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
        val createdAt = LocalDateTime.parse(it.createdAt, formatter)
        NotificationLog(type, nickname, createdAt)
    }
