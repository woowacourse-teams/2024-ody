package com.woowacourse.ody.domain.model

import java.time.LocalDateTime

data class ReserveInfo(
    val meetingId: Long,
    val meetingDateTime: LocalDateTime,
)
