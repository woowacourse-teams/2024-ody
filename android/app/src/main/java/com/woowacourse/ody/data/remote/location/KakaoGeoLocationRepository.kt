package com.woowacourse.ody.data.remote.location

import com.woowacourse.ody.BuildConfig
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.domain.GeoLocationRepository
import kotlin.concurrent.thread

object KakaoGeoLocationRepository : GeoLocationRepository {
    override fun fetchGeoLocation(address: String): GeoLocation {
        var geoCoordinate: GeoLocation? = null
        thread {
            val result =
                KakaoRetrofitClient.retrofit.create(KakaoLocationService::class.java)
                    .fetchLocation(
                        key = "KakaoAK ${BuildConfig.KAKAO_API_KEY}",
                        query = address,
                    ).execute().body()!!
            val longitude: String = result.documents[0].x
            val latitude: String = result.documents[0].y
            geoCoordinate = GeoLocation(address = address, longitude = longitude, latitude = latitude)
        }.join()
        return geoCoordinate!!
    }
}
