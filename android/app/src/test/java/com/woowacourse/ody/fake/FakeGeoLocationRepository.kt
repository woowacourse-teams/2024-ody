package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.repository.location.GeoLocationRepository

object FakeGeoLocationRepository : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
        return ApiResult.Success(GeoLocation(address = address, longitude = "10.0", latitude = "10.0"))
    }
}
