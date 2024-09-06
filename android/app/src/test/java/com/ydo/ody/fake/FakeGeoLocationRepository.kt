package com.ydo.ody.fake

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.GeoLocation
import com.ydo.ody.domain.repository.location.GeoLocationRepository

object FakeGeoLocationRepository : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
        return ApiResult.Success(GeoLocation(address = address, longitude = "10.0", latitude = "10.0"))
    }
}
