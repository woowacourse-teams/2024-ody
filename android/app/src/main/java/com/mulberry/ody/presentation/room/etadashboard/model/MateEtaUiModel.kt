package com.mulberry.ody.presentation.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val etaStatusUiModel: EtaStatusUiModel,
    val isMissing: Boolean,
    val isUserSelf: Boolean,
    val userId: Long,
    val mateId: Long,
)
