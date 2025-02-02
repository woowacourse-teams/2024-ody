package com.mulberry.ody.presentation.room.etadashboard

import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.getPointOnScreen
import com.mulberry.ody.presentation.common.listener.setOnSingleClickListener
import com.mulberry.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.mulberry.ody.presentation.room.etadashboard.listener.NudgeListener
import com.mulberry.ody.presentation.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.room.etadashboard.model.MateEtaUiModel

@BindingAdapter("etaType")
fun TextView.setBadgeByEtaType(etaStatusUiModel: EtaStatusUiModel) {
    text = context.getString(etaStatusUiModel.badgeMessageId)
    backgroundTintList = ContextCompat.getColorStateList(context, etaStatusUiModel.badgeColorId)
}

@BindingAdapter("etaStatus")
fun TextView.setEtaStatusText(etaStatusUiModel: EtaStatusUiModel) {
    text = etaStatusUiModel.etaStatusMessage(context)
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
fun TextView.setEtaBadgeAnimation(etaStatusUiModel: EtaStatusUiModel) {
    if (etaStatusUiModel.canNudge()) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_duration_500)
        this.startAnimation(animation)
    }
}

@BindingAdapter("etaBadgeAnimation")
fun TextView.setEtaBadgeAnimation(canNudge: Boolean) {
    if (canNudge) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.bounce_duration_500)
        this.startAnimation(animation)
    }
}

@BindingAdapter("canNudge", "nudgeListener")
fun TextView.setOnClickNudge(
    mateEtaUiModel: MateEtaUiModel,
    nudgeListener: NudgeListener,
) {
    setOnSingleClickListener {
        if (mateEtaUiModel.isUserSelf.not() && mateEtaUiModel.etaStatusUiModel.canNudge()) {
            nudgeListener.nudgeMate(
                nudgeId = mateEtaUiModel.userId,
                mateId = mateEtaUiModel.mateId,
            )
        }
    }
}
