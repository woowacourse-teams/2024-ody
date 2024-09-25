package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Address

interface AddressRepository {
    suspend fun fetchAddresses(keyword: String): ApiResult<List<Address>>
}
