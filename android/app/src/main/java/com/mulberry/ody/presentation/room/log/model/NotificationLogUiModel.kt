package com.mulberry.ody.presentation.room.log.model

import com.mulberry.ody.domain.model.NotificationLogType

data class NotificationLogUiModel(
    val type: NotificationLogType,
    val nickname: String,
    val created: String,
    val imageUrl: String,
)
