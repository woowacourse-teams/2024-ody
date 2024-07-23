package com.woowacourse.ody.data.remote.location.response

import com.woowacourse.ody.domain.GeoLocation

fun LocationSearchResponse.toGeoLocation(address: String): GeoLocation {
    return GeoLocation(address, documents[0].x, documents[0].y)
}
