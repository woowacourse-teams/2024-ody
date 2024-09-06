package com.ydo.ody.presentation.room.etadashboard

import android.graphics.Point
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.ydo.ody.R
import com.ydo.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.ydo.ody.presentation.room.etadashboard.listener.NudgeListener
import com.ydo.ody.presentation.room.etadashboard.model.EtaDurationMinuteTypeUiModel
import com.ydo.ody.presentation.room.etadashboard.model.EtaTypeUiModel
import com.ydo.ody.presentation.room.etadashboard.model.MateEtaUiModel

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
            EtaDurationMinuteTypeUiModel.ARRIVAL_REMAIN_TIME ->
                context.getString(
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

@BindingAdapter("etaBadgeAnimation")
fun TextView.setEtaBadgeAnimation(etaTypeUiModel: EtaTypeUiModel) {
    if (
        etaTypeUiModel == EtaTypeUiModel.LATE ||
        etaTypeUiModel == EtaTypeUiModel.LATE_WARNING
    ) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_duration_500)
        this.startAnimation(animation)
    }
}

@BindingAdapter("canNudge", "nudgeListener")
fun TextView.setOnClickNudge(
    mateEtaUiModel: MateEtaUiModel,
    nudgeListener: NudgeListener,
) {
    setOnClickListener {
        if (
            mateEtaUiModel.isUserSelf.not() &&
            (mateEtaUiModel.etaTypeUiModel == EtaTypeUiModel.LATE || mateEtaUiModel.etaTypeUiModel == EtaTypeUiModel.LATE_WARNING)
        ) {
            nudgeListener.nudgeMate(
                nudgeId = mateEtaUiModel.userId,
                mateId = mateEtaUiModel.mateId,
            )
        }
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
