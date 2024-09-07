package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.GeoLocation

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation>
}
