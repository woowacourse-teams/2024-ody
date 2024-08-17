package com.woowacourse.ody.data.remote.thirdparty.location.repository

import com.woowacourse.ody.data.remote.thirdparty.location.entity.response.toGeoLocation
import com.woowacourse.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.apiresult.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.repository.location.GeoLocationRepository

class KakaoGeoLocationRepository(private val service: KakaoLocationService) : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
        return service.fetchLocation(address).map { it.toGeoLocation(address) }
    }
}
