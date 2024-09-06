package com.ydo.ody.data.remote.thirdparty.location.entity.response

import com.ydo.ody.domain.model.GeoLocation

fun LocationSearchResponse.toGeoLocation(address: String): GeoLocation {
    return GeoLocation(address, documents[0].x, documents[0].y)
}
