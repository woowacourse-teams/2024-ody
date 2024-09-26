package com.mulberry.ody.fake

import com.mulberry.ody.addresses
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository

object FakeAddressRepository : AddressRepository {
    override suspend fun fetchAddresses(
        keyword: String,
        pageSize: Int,
    ): ApiResult<List<Address>> {
        return ApiResult.Success(addresses)
    }
}
