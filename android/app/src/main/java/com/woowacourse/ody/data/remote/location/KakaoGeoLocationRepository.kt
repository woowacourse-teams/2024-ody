package com.woowacourse.ody.data.remote.location

import com.woowacourse.ody.BuildConfig
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.domain.GeoLocationRepository

object KakaoGeoLocationRepository : GeoLocationRepository {
    private val service = KakaoRetrofitClient.retrofit.create(KakaoLocationService::class.java)

    override suspend fun fetchGeoLocation(address: String): Result<GeoLocation> {
        return runCatching {
            val locationSearchResponse = service.fetchLocation(
                key = "KakaoAK ${BuildConfig.KAKAO_API_KEY}",
                query = address,
            )
            val longitude = locationSearchResponse.documents[0].x
            val latitude = locationSearchResponse.documents[0].y
            return Result.success(GeoLocation(address, longitude, latitude))
        }
    }
}
