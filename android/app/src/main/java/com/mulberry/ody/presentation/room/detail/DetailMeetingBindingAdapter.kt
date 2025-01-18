package com.mulberry.ody.presentation.room.detail

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.presentation.common.getPointOnScreen
import com.mulberry.ody.presentation.room.detail.listener.DepartureTimeGuideListener

@BindingAdapter("onClickDepartureTimeGuide")
fun ImageView.setOnClickDepartureTimeGuide(
    missingToolTipListener: DepartureTimeGuideListener,
) {
    setOnClickListener {
        val point = this.getPointOnScreen()
        missingToolTipListener.toggleDepartureTimeGuide(point)
    }
}
