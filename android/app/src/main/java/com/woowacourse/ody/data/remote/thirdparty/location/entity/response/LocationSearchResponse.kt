package com.woowacourse.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.GeoLocation

@JsonClass(generateAdapter = true)
data class LocationSearchResponse(
    @Json(name = "documents") val documents: List<Document>,
    @Json(name = "meta") val meta: Meta,
) {
    fun toGeoLocation(address: String): GeoLocation =
        GeoLocation(
            address,
            documents[0].x,
            documents[0].y,
        )
}
