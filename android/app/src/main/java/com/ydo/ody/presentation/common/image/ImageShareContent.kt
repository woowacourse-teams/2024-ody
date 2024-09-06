package com.ydo.ody.presentation.common.image

data class ImageShareContent(
    val title: String,
    val description: String? = null,
    val buttonTitle: String,
    val imageUrl: String,
    val imageWidthPixel: Int? = null,
    val imageHeightPixel: Int? = null,
    val link: String,
)
