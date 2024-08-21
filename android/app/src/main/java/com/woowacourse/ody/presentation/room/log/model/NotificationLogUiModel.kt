package com.woowacourse.ody.presentation.room.log.model

import com.woowacourse.ody.domain.model.NotificationType

data class NotificationLogUiModel(
    val type: NotificationType,
    val nickname: String,
    val created: String,
    val imageUrl: String,
)
