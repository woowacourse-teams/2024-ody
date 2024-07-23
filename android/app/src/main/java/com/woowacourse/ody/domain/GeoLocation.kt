package com.woowacourse.ody.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class GeoLocation(
    val address: String,
    val longitude: String,
    val latitude: String,
) : Parcelable
