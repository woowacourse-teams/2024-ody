package com.mulberry.ody.data.remote.core.entity.notification.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationLogsResponse(
    @SerialName("notiLog")
    val logList: List<NotificationLogResponse>,
)
