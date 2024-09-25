package com.mulberry.ody.data.remote.thirdparty.address

import com.mulberry.ody.data.remote.thirdparty.address.response.toAddresses
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import javax.inject.Inject

class KakaoAddressRepository
    @Inject
    constructor(private val service: KakaoAddressService) : AddressRepository {
        override suspend fun fetchAddresses(keyword: String): ApiResult<List<Address>> {
            return service.fetchAddresses(keyword).map { it.toAddresses() }
        }
    }