package com.woowacourse.ody.domain.model

import java.time.LocalDateTime

data class NotificationLog(
    val type: NotificationType,
    val nickname: String,
    val createdAt: LocalDateTime,
    val imageUrl: String = "https://i.namu.wiki/i/d1A_wD4kuLHmOOFqJdVlOXVt1TWA9NfNt_HA0CS0Y_N0zayUAX8olMuv7odG2FiDLDQZIRBqbPQwBSArXfEJlQ.webp"
    // todo: 추후 imageUrl의 default value 제거
)
