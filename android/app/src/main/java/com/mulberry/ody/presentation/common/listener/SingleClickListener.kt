package com.mulberry.ody.presentation.common.listener

import android.view.View

class SingleClickListener(
    private var interval: Long = 600L,
    private val onSingleClick: () -> Unit,
) : View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(view: View) {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < interval) {
            return
        }
        lastClickTime = now
        onSingleClick()
    }
}

fun View.setOnSingleClickListener(onSingleClick: View.OnClickListener) {
    val singleClickListener = SingleClickListener { onSingleClick.onClick(this) }
    setOnClickListener(singleClickListener)
}
