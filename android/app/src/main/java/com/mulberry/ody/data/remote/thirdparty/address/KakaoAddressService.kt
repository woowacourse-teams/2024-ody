package com.mulberry.ody.data.remote.thirdparty.address

import com.mulberry.ody.data.remote.thirdparty.address.response.AddressResponse
import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAddressService {
    @GET("/v2/local/search/keyword.json")
    suspend fun fetchAddresses(
        @Query("query") keyword: String,
        @Query("size") size: Int = 10,
    ): ApiResult<AddressResponse>
}
