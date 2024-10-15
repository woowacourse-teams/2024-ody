package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Address

interface AddressRepository {
    suspend fun fetchAddresses(
        keyword: String,
        pageSize: Int,
    ): ApiResult<List<Address>>

    suspend fun fetchAddressesByCoordinate(
        x: String,
        y: String,
    ): ApiResult<String?>
}
