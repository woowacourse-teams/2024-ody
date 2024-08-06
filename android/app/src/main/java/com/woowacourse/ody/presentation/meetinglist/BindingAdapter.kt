package com.woowacourse.ody.presentation.meetinglist

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter
fun TextView.setVisible(isVisible: Boolean) {
    visibility =
        if (isVisible) {
            TextView.VISIBLE
        } else {
            TextView.GONE
        }
}
