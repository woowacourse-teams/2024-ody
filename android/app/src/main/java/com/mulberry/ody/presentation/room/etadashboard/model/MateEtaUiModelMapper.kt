package com.mulberry.ody.presentation.room.etadashboard.model

import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEtaInfo

fun MateEtaInfo.toMateEtaUiModels(): List<MateEtaUiModel> {
    return mateEtas.map { it.toMateEtaUiModel(userId) }
}

private fun MateEta.toMateEtaUiModel(userId: Long): MateEtaUiModel {
    return MateEtaUiModel(
        nickname = nickname,
        etaStatusUiModel = etaStatus.toEtaStatusUiModel(),
        isMissing = etaStatus is EtaStatus.Missing,
        isUserSelf = userId == mateId,
        userId = userId,
        mateId = mateId,
    )
}
