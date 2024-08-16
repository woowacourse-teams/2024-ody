package com.woowacourse.ody.presentation.room.etadashboard.model

import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.MateEtaInfo

fun MateEtaInfo.toMateEtaUiModels(): List<MateEtaUiModel> {
    return mateEtas.map { it.toMateEtaUiModel(userNickname) }
}

private fun MateEta.toMateEtaUiModel(userNickname: String): MateEtaUiModel {
    return MateEtaUiModel(
        nickname = nickname,
        etaTypeUiModel = etaType.toEtaTypeUiModel(),
        durationMinute = durationMinute.toInt(),
        isMissing = etaType == EtaType.MISSING,
        isUserSelf = userNickname == nickname,
    )
}
