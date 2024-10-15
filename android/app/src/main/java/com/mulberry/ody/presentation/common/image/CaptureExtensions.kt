package com.mulberry.ody.presentation.common.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

fun RecyclerView.getBitmap(): Bitmap {
    val height = this.getItemsHeight()
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

private fun RecyclerView.getItemsHeight(): Int {
    val adapter = adapter ?: return 0
    val itemCount = adapter.itemCount
    if (itemCount == 0) return 0

    var totalHeight = 0

    for (i in 0 until itemCount) {
        val viewType = adapter.getItemViewType(i)
        val viewHolder = adapter.createViewHolder(this, viewType)
        adapter.onBindViewHolder(viewHolder, i)

        viewHolder.itemView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED,
        )

        totalHeight += viewHolder.itemView.measuredHeight
    }

    return totalHeight
}

fun Bitmap.toByteArray(): ByteArray {
    return ByteArrayOutputStream().use { byteArrayOutputStream ->
        compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        byteArrayOutputStream.toByteArray()
    }
}
