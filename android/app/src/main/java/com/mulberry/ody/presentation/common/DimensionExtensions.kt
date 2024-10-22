package com.mulberry.ody.presentation.common

import android.content.Context

fun Int.toPixel(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}
