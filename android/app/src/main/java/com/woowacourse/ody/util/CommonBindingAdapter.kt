package com.woowacourse.ody.util

import android.widget.NumberPicker
import androidx.databinding.BindingAdapter

object CommonBindingAdapter {
    @BindingAdapter("values")
    @JvmStatic
    fun NumberPicker.setDisplayedValues(values: List<String>?) {
        values ?: return
        minValue = 0
        maxValue = values.size - 1
        displayedValues = values.toTypedArray()
    }
}
