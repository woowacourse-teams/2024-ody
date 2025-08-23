package com.mulberry.ody.presentation.common.image

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray {
    return ByteArrayOutputStream().use { byteArrayOutputStream ->
        compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        byteArrayOutputStream.toByteArray()
    }
}
