package com.woowacourse.ody.presentation.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val etaTypeUiModel: EtaTypeUiModel,
    val durationMinute: Int,
    val isMissing: Boolean,
    val isUserSelf: Boolean,
)
