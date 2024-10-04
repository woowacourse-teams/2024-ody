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
        override suspend fun fetchAddresses(
            keyword: String,
            pageSize: Int,
        ): ApiResult<List<Address>> {
            return service.fetchAddresses(keyword, pageSize).map { it.toAddresses() }
        }

        override suspend fun fetchAddressesByCoordinate(
            x: String,
            y: String,
        ): ApiResult<String?> {
            return service.fetchAddressesByCoord(x, y).map {
                if (it.documents.isNotEmpty()) {
                    it.documents[0].address?.addressName
                } else {
                    null
                }
            }
        }
    }
