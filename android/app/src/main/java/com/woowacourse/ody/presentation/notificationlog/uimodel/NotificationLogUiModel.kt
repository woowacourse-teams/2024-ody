package com.woowacourse.ody.presentation.notificationlog.uimodel

import com.woowacourse.ody.domain.NotificationType

data class NotificationLogUiModel(
    val type: NotificationType,
    val nickname: String,
    val created: String,
)
