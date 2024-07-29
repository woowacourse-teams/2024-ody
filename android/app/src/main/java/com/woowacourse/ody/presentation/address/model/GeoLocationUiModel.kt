package com.woowacourse.ody.presentation.address.model

import android.os.Parcelable
import com.woowacourse.ody.domain.model.GeoLocation
import kotlinx.parcelize.Parcelize

@Parcelize
class GeoLocationUiModel(
    val address: String,
    val longitude: String,
    val latitude: String,
) : Parcelable {
    fun toGeoLocation(): GeoLocation = GeoLocation(address, longitude, latitude)
}
