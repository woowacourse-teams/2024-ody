package com.woowacourse.ody.presentation.address.model

import com.woowacourse.ody.domain.model.GeoLocation

fun GeoLocationUiModel.toGeoLocation(): GeoLocation =
    GeoLocation(
        address,
        longitude,
        latitude,
    )

fun GeoLocation.toGeoLocationUiModel(): GeoLocationUiModel =
    GeoLocationUiModel(
        address,
        longitude,
        latitude,
    )
