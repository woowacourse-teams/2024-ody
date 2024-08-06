package com.woowacourse.ody.presentation.room.etadashboard.listener

import android.graphics.Point

interface MissingToolTipListener {
    fun onClickMissingToolTipListener(
        point: Point,
        isUserSelf: Boolean,
    )
}
