package com.mulberry.ody.presentation.common.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import java.io.ByteArrayOutputStream

fun Picture.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawPicture(this)
    return bitmap
}

fun Bitmap.toByteArray(): ByteArray {
    return ByteArrayOutputStream().use { byteArrayOutputStream ->
        compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        byteArrayOutputStream.toByteArray()
    }
}
