package com.woowacourse.ody.domain.repository.location

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.GeoLocation

interface GeoLocationRepository {
    suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation>
}
