package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Addresses

interface AddressRepository {
    suspend fun fetchAddresses(
        keyword: String,
        page: Int,
        pageSize: Int,
    ): ApiResult<Addresses>

    suspend fun fetchAddressNameByCoordinate(
        x: String,
        y: String,
    ): ApiResult<String?>
}
