package com.mulberry.ody.presentation.feature.room.log.model

import com.mulberry.ody.domain.model.NotificationLog
import java.time.format.DateTimeFormatter

fun NotificationLog.toNotificationLogUiModel(): NotificationLogUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return NotificationLogUiModel(
        type,
        nickname,
        createdAt.format(dateTimeFormatter),
        imageUrl,
    )
}

fun List<NotificationLog>.toNotificationLogUiModels(): List<NotificationLogUiModel> = map { it.toNotificationLogUiModel() }
