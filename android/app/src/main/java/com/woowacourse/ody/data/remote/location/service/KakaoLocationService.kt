package com.woowacourse.ody.data.remote.location.service

import com.woowacourse.ody.data.remote.location.entity.response.LocationSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocationService {
    @GET("v2/local/search/address.json")
    suspend fun fetchLocation(
        @Query("query") query: String,
    ): LocationSearchResponse
}
