package com.woowacourse.ody.data.remote.location

import com.woowacourse.ody.data.remote.location.response.toGeoLocation
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.domain.GeoLocationRepository

object KakaoGeoLocationRepository : GeoLocationRepository {
    private val service = KakaoRetrofitClient.retrofit.create(KakaoLocationService::class.java)

    override suspend fun fetchGeoLocation(address: String): Result<GeoLocation> {
        return runCatching {
            val locationSearchResponse = service.fetchLocation(address)
            locationSearchResponse.toGeoLocation(address)
        }
    }
}
