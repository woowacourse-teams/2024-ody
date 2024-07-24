package com.woowacourse.ody.util

import android.view.View
import androidx.databinding.BindingAdapter

object CommonBindingAdapter {
    @BindingAdapter("visibility")
    @JvmStatic
    fun View.setVisibility(isVisible: Boolean?) {
        visibility = if (isVisible == true) View.VISIBLE else View.GONE
    }
}
