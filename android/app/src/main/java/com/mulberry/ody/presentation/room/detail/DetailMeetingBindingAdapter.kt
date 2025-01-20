package com.mulberry.ody.presentation.room.detail

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.getPointOnScreen
import com.mulberry.ody.presentation.room.detail.listener.DepartureTimeGuideListener
import com.mulberry.ody.presentation.room.detail.model.DetailMeetingUiModel

@BindingAdapter("onClickDepartureTimeGuide")
fun ImageView.setOnClickDepartureTimeGuide(missingToolTipListener: DepartureTimeGuideListener) {
    setOnClickListener {
        val point = this.getPointOnScreen()
        missingToolTipListener.toggleDepartureTimeGuide(point)
    }
}

@BindingAdapter("departureTime")
fun TextView.setDepartureTimeText(detailMeetingUiModel: DetailMeetingUiModel) {
    text =
        context.getString(
            R.string.detail_meeting_departure_time_content,
            detailMeetingUiModel.departureTime,
            detailMeetingUiModel.routeTime,
        )
}
