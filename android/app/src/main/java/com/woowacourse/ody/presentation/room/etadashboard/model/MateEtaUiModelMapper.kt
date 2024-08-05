package com.woowacourse.ody.presentation.room.etadashboard.model

import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.MateEta

fun MateEta.toMateEtaUiModel(): MateEtaUiModel {
    return MateEtaUiModel(
        nickname = nickname,
        etaTypeUiModel = etaType.toEtaTypeUiModel(),
        durationMinute = durationMinute.toInt(),
        isMissing = etaType == EtaType.MISSING,
    )
}
