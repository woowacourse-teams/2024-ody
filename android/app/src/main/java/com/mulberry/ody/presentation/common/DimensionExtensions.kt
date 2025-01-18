package com.mulberry.ody.presentation.common

import android.content.Context
import android.graphics.Point
import android.view.View

fun Int.toPixel(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density).toInt()
}

fun View.getPointOnScreen(): Point {
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    return Point().apply {
        x = location[0]
        y = location[1]
    }
}
