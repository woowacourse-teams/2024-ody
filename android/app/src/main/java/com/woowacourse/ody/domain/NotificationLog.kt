package com.woowacourse.ody.domain

import java.time.LocalDateTime

data class NotificationLog(
    val type: NotificationType,
    val nickname: String,
    val createdAt: LocalDateTime,
)
