package com.ydo.ody.presentation.room.log.model

import com.ydo.ody.domain.model.NotificationType

data class NotificationLogUiModel(
    val type: NotificationType,
    val nickname: String,
    val created: String,
    val imageUrl: String,
)
