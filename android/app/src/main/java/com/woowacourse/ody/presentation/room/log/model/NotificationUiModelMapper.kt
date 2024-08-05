package com.woowacourse.ody.presentation.room.log.model

import com.woowacourse.ody.domain.model.NotificationLog
import java.time.format.DateTimeFormatter

fun NotificationLog.toNotificationUiModel(): NotificationLogUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return NotificationLogUiModel(
        type,
        nickname,
        createdAt.format(dateTimeFormatter),
    )
}

fun List<NotificationLog>.toNotificationUiModels(): List<NotificationLogUiModel> = this.map { it.toNotificationUiModel() }
