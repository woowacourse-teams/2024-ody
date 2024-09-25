package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Location

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): ApiResult<Location>
}
