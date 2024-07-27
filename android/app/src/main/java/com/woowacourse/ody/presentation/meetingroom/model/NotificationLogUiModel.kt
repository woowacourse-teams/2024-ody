package com.woowacourse.ody.presentation.meetingroom.model

import com.woowacourse.ody.domain.model.NotificationType

data class NotificationLogUiModel(
    val type: NotificationType,
    val nickname: String,
    val created: String,
)
