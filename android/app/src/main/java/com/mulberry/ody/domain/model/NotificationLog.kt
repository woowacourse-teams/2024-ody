package com.mulberry.ody.domain.model

import java.time.LocalDateTime

data class NotificationLog(
    val type: LogType,
    val nickname: String,
    val createdAt: LocalDateTime,
    val imageUrl: String,
)
