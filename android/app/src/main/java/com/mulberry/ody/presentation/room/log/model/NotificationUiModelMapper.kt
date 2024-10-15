package com.mulberry.ody.presentation.room.log.model

import com.mulberry.ody.domain.model.NotificationLog
import java.time.format.DateTimeFormatter

fun NotificationLog.toNotificationUiModel(): NotificationLogUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return NotificationLogUiModel(
        type,
        nickname,
        createdAt.format(dateTimeFormatter),
        imageUrl,
    )
}

fun List<NotificationLog>.toNotificationUiModels(): List<NotificationLogUiModel> = this.map { it.toNotificationUiModel() }
