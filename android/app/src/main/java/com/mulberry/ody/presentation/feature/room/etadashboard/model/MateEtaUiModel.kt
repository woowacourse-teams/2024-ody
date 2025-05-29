package com.mulberry.ody.presentation.feature.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val etaStatusUiModel: EtaStatusUiModel,
    val isUserSelf: Boolean,
    val userId: Long,
    val mateId: Long,
)
