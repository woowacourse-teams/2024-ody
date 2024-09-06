package com.ydo.ody.domain.repository.location

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.GeoLocation

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation>
}
