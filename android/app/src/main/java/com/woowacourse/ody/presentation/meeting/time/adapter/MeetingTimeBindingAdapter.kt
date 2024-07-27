package com.woowacourse.ody.presentation.meeting.time.adapter

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter

object MeetingTimeBindingAdapter {
    @BindingAdapter("timeValues")
    @JvmStatic
    fun NumberPicker.setTimeValues(values: List<Int>?) {
        values ?: return
        val timeValues = values.map { it.toTwoLengthString() }
        minValue = 0
        maxValue = timeValues.size - 1
        displayedValues = timeValues.toTypedArray()
    }

    private fun Int.toTwoLengthString(): String {
        val string = toString()
        return if (string.length == 1) "0$string" else string
    }
}
