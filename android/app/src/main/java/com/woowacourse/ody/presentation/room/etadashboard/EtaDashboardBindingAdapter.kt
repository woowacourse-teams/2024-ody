package com.woowacourse.ody.presentation.room.etadashboard

import android.graphics.Point
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.woowacourse.ody.presentation.room.etadashboard.model.EtaDurationMinuteTypeUiModel
import com.woowacourse.ody.presentation.room.etadashboard.model.EtaTypeUiModel
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel

@BindingAdapter("etaType")
fun TextView.setBadgeByEtaType(etaTypeUiModel: EtaTypeUiModel) {
    text = context.getString(etaTypeUiModel.messageId)
    backgroundTintList = ContextCompat.getColorStateList(context, etaTypeUiModel.colorId)
}

@BindingAdapter("etaStatus")
fun TextView.setEtaStatusText(mateEtaUiModel: MateEtaUiModel) {
    text =
        when (mateEtaUiModel.getEtaDurationMinuteTypeUiModel()) {
            EtaDurationMinuteTypeUiModel.ARRIVED -> context.getString(R.string.status_arrived)
            EtaDurationMinuteTypeUiModel.MISSING -> context.getString(R.string.status_missing)
            EtaDurationMinuteTypeUiModel.ARRIVAL_SOON -> context.getString(R.string.status_arrival_soon)
            EtaDurationMinuteTypeUiModel.ARRIVAL_REMAIN_TIME -> context.getString(
                R.string.status_arrival_remain_time,
                mateEtaUiModel.durationMinute,
            )
        }
}

@BindingAdapter("isUserSelf", "missingToolTipListener")
fun TextView.setOnClickMissingTooltip(
    isUserSelf: Boolean,
    missingToolTipListener: MissingToolTipListener,
) {
    setOnClickListener {
        val point = this.getPointOnScreen()
        missingToolTipListener.onClickMissingToolTipListener(point, isUserSelf)
    }
}

private fun View.getPointOnScreen(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return Point().apply {
        x = location[0]
        y = location[1]
    }
}
