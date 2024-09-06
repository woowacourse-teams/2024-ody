package com.ydo.ody.presentation.room.etadashboard.listener

import android.graphics.Point

interface MissingToolTipListener {
    fun onClickMissingToolTipListener(
        point: Point,
        isUserSelf: Boolean,
    )
}
