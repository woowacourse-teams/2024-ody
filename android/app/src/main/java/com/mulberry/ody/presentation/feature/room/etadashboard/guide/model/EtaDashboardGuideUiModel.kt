package com.mulberry.ody.presentation.feature.room.etadashboard.guide.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel

class EtaDashboardGuideUiModel(
    val mateEtas: List<MateEtaUiModel>,
    @StringRes val nudgeMessageId: Int,
    @StringRes val messageId: Int,
    @StringRes val buttonMessageId: Int,
    @DrawableRes val buttonImageId: Int,
)
