package com.mulberry.ody.data.remote.thirdparty.location.entity.response

import com.mulberry.ody.domain.model.GeoLocation

fun LocationSearchResponse.toGeoLocation(address: String): GeoLocation {
    return GeoLocation(address, documents[0].x, documents[0].y)
}
