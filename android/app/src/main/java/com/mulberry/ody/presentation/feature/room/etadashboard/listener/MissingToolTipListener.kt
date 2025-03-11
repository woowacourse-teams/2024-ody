package com.mulberry.ody.presentation.feature.room.etadashboard.listener

import android.graphics.Point

interface MissingToolTipListener {
    fun onClickMissingToolTipListener(
        point: Point,
        isUserSelf: Boolean,
    )
}
