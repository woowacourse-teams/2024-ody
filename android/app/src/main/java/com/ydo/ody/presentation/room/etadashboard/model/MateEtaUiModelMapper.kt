package com.ydo.ody.presentation.room.etadashboard.model

import com.ydo.ody.domain.model.EtaType
import com.ydo.ody.domain.model.MateEta
import com.ydo.ody.domain.model.MateEtaInfo

fun MateEtaInfo.toMateEtaUiModels(): List<MateEtaUiModel> {
    return mateEtas.map { it.toMateEtaUiModel(userId) }
}

private fun MateEta.toMateEtaUiModel(userId: Long): MateEtaUiModel {
    return MateEtaUiModel(
        nickname = nickname,
        etaTypeUiModel = etaType.toEtaTypeUiModel(),
        durationMinute = durationMinute,
        isMissing = etaType == EtaType.MISSING,
        isUserSelf = userId == mateId,
        userId = userId,
        mateId = mateId,
    )
}
