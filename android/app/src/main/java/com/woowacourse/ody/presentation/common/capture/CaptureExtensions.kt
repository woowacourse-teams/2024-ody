package com.woowacourse.ody.presentation.common.capture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.ByteArrayOutputStream

fun View.getBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

fun Bitmap.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}
