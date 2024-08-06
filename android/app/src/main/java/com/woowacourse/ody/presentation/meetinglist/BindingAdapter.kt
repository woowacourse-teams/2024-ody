package com.woowacourse.ody.presentation.meetinglist

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("setVisible")
fun View.setVisible(isVisible: Boolean) {
    visibility =
        if (isVisible) {
            TextView.VISIBLE
        } else {
            TextView.GONE
        }
}
