package com.mulberry.ody.presentation.feature.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val etaStatusUiModel: EtaStatusUiModel,
    val isMissing: Boolean,
    val isUserSelf: Boolean,
    val userId: Long,
    val mateId: Long,
)
