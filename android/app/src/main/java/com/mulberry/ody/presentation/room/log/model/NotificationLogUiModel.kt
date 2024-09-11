package com.mulberry.ody.presentation.room.log.model

import com.mulberry.ody.domain.model.LogType

data class NotificationLogUiModel(
    val type: LogType,
    val nickname: String,
    val created: String,
    val imageUrl: String,
)
