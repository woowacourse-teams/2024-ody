package com.mulberry.ody.presentation.feature.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val status: EtaStatusUiModel,
    val userId: Long,
    val mateId: Long,
) {
    val isMissing: Boolean = status == EtaStatusUiModel.Missing

    val isUserSelf: Boolean = userId == mateId

    val canNudge: Boolean = !isUserSelf && status.canNudge()
}
