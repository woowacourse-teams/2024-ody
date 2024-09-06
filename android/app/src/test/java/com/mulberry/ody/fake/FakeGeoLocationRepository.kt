package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.GeoLocation
import com.mulberry.ody.domain.repository.location.GeoLocationRepository

object FakeGeoLocationRepository : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
        return ApiResult.Success(GeoLocation(address = address, longitude = "10.0", latitude = "10.0"))
    }
}
