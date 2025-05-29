package com.mulberry.ody.presentation.feature.room.etadashboard.model

import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEtaInfo

fun MateEtaInfo.toMateEtaUiModels(): List<MateEtaUiModel> {
    return mateEtas.map { it.toMateEtaUiModel(userId) }
}

private fun MateEta.toMateEtaUiModel(userId: Long): MateEtaUiModel {
    return MateEtaUiModel(
        nickname = nickname,
        etaStatusUiModel = etaStatus.toEtaStatusUiModel(),
        isUserSelf = userId == mateId,
        userId = userId,
        mateId = mateId,
    )
}
