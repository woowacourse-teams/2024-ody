package com.woowacourse.ody.domain.model

import java.time.LocalDateTime

data class NotificationLog(
    val type: NotificationType,
    val nickname: String,
    val createdAt: LocalDateTime,
    val imageUrl: String = "https://t3.gstatic.com/licensed-image?q=tbn:ANd9GcREpFdbEdAPEsaWwOf12kUS_A4Tjs923CZQ5N80PX2e-woBBeE_EMrFaA4QAmgAUnLT"
    // todo: 추후 imageUrl의 default value 제거
)
