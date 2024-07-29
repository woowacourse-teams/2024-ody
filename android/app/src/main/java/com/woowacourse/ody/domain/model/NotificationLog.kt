package com.woowacourse.ody.domain.model

import com.woowacourse.ody.presentation.room.model.NotificationLogUiModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NotificationLog(
    val type: NotificationType,
    val nickname: String,
    val createdAt: LocalDateTime,
) {
    fun toNotificationUiModel(): NotificationLogUiModel {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return NotificationLogUiModel(
            this.type,
            this.nickname,
            this.createdAt.format(dateTimeFormatter),
        )
    }
}
