package com.woowacourse.ody.presentation.room.etadashboard.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.woowacourse.ody.R

enum class EtaTypeUiModel(
    @ColorRes private val colorId: Int,
    @StringRes private val messageId: Int,
) {
    LATE_WARNING(R.color.yellow, R.string.badge_late_warning),
    ARRIVAL_SOON(R.color.green, R.string.badge_arrival_soon),
    ARRIVED(R.color.blue, R.string.badge_arrived),
    LATE(R.color.red, R.string.badge_late),
    MISSING(R.color.gray_400, R.string.badge_missing),
}
