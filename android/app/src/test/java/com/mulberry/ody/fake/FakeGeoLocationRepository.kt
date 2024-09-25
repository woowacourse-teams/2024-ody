package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Location
import com.mulberry.ody.domain.repository.location.GeoLocationRepository

object FakeGeoLocationRepository : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<Location> {
        return ApiResult.Success(Location(address = address, longitude = "10.0", latitude = "10.0"))
    }
}
