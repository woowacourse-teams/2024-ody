package com.woowacourse.ody.presentation.room.etadashboard.model

import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.MateEta

fun List<MateEta>.toMateEtaUiModels(userNickname: String): List<MateEtaUiModel> {
    return this.map { it.toMateEtaUiModel(userNickname) }
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
