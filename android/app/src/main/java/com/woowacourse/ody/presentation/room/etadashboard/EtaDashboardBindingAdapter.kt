package com.woowacourse.ody.presentation.room.etadashboard

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.woowacourse.ody.presentation.room.etadashboard.model.EtaTypeUiModel

@BindingAdapter("etaType")
fun TextView.setBadgeByEtaType(etaTypeUiModel: EtaTypeUiModel) {
    text = context.getString(etaTypeUiModel.messageId)
    backgroundTintList = ContextCompat.getColorStateList(context, etaTypeUiModel.colorId)
}

@BindingAdapter("etaStatus")
fun TextView.setEtaStatusText(durationMinute: Int) {
    text =
        when (durationMinute) {
            0 -> context.getString(R.string.status_arrived)
            -1 -> context.getString(R.string.status_missing)
            in 1..10 -> context.getString(R.string.status_arrival_soon)
            else -> context.getString(R.string.status_arrival_remain_time, durationMinute)
        }
}

@BindingAdapter("isUserSelf", "missingToolTipListener")
fun TextView.setOnClickMissingTooltip(
    isUserSelf: Boolean,
    missingToolTipListener: MissingToolTipListener,
) {
    setOnClickListener {
        val (x, y) = this.getPointOnScreen()
        missingToolTipListener.onClickMissingToolTipListener(x, y, isUserSelf)
    }
}

private fun View.getPointOnScreen(): Pair<Int, Int> {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return location[0] to location[1]
}
