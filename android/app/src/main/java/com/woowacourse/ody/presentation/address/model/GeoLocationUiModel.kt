package com.woowacourse.ody.presentation.address.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class GeoLocationUiModel(
    val address: String,
    val longitude: String,
    val latitude: String,
) : Parcelable
