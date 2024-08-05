package com.woowacourse.ody.presentation.room.etadashboard

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
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
