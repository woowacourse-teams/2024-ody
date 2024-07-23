package com.woowacourse.ody.domain

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): Result<GeoLocation>
}
