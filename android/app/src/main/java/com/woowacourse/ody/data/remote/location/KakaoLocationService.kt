package com.woowacourse.ody.data.remote.location

import com.woowacourse.ody.data.remote.location.response.LocationSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocationService {
    @GET("v2/local/search/address.json")
    fun fetchLocation(
        @Header("Authorization") key: String,
        @Query("query") query: String
    ): Call<LocationSearchResponse>
}
