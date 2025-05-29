package com.mulberry.ody.presentation.feature.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val status: EtaStatusUiModel,
    val isUserSelf: Boolean,
    val userId: Long,
    val mateId: Long,
) {
    val isMissing: Boolean get() = status == EtaStatusUiModel.Missing
}
