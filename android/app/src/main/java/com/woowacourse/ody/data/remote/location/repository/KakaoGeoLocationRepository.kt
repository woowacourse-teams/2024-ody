package com.woowacourse.ody.data.remote.location.repository

import com.woowacourse.ody.data.remote.location.KakaoRetrofitClient
import com.woowacourse.ody.data.remote.location.entity.response.toGeoLocation
import com.woowacourse.ody.data.remote.location.service.KakaoLocationService
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.repository.GeoLocationRepository

object KakaoGeoLocationRepository : GeoLocationRepository {
    private val service = KakaoRetrofitClient.retrofit.create(KakaoLocationService::class.java)

    override suspend fun fetchGeoLocation(address: String): Result<GeoLocation> {
        return runCatching {
            val locationSearchResponse = service.fetchLocation(address)
            locationSearchResponse.toGeoLocation(address)
        }
    }
}
