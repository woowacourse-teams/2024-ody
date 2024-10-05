package com.mulberry.ody.data.remote.thirdparty.address

import com.mulberry.ody.data.remote.thirdparty.address.response.AddressResponse
import com.mulberry.ody.data.remote.thirdparty.address.response.coord.AddressByCoordinateResponse
import com.mulberry.ody.domain.apiresult.ApiResult
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAddressService {
    @GET("/v2/local/search/keyword.json")
    suspend fun fetchAddresses(
        @Query("query") keyword: String,
        @Query("size") size: Int,
    ): ApiResult<AddressResponse>

    @GET("/v2/local/geo/coord2address.json")
    suspend fun fetchAddressesByCoordinate(
        @Query("x") x: String,
        @Query("y") y: String,
    ): ApiResult<AddressByCoordinateResponse>
}
