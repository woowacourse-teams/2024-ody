package com.mulberry.ody.presentation.room.log.model

import com.mulberry.ody.domain.model.NotificationType

data class NotificationLogUiModel(
    val type: NotificationType,
    val nickname: String,
    val created: String,
    val imageUrl: String,
)
