package com.woowacourse.ody.domain.model

import com.woowacourse.ody.presentation.address.model.GeoLocationUiModel

class GeoLocation(
    val address: String,
    val longitude: String,
    val latitude: String,
) {
    fun toGeoLocationUiModel(): GeoLocationUiModel = GeoLocationUiModel(address, longitude, latitude)
}
