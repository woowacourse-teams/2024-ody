package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.domain.model.GeoLocation

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): Result<GeoLocation>
}
