package com.woowacourse.ody.presentation.common.image

data class ImageShareContent(
    val description: String,
    val buttonTitle: String,
    val imageUrl: String,
    val imageWidthPixel: Int,
    val imageHeightPixel: Int,
    val link: String,
)
