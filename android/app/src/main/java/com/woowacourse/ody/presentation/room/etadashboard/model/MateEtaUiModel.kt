package com.woowacourse.ody.presentation.room.etadashboard.model

data class MateEtaUiModel(
    private val nickname:String,
    private val etaTypeUiModel: EtaTypeUiModel,
    private val durationMinute: Int,
)
