package com.woowacourse.ody.domain

interface GeoLocationRepository {
    fun fetchGeoLocation(address: String): GeoLocation
}
