package com.mulberry.ody.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mulberry.ody.data.remote.thirdparty.address.AddressPagingSource
import com.mulberry.ody.data.remote.thirdparty.address.KakaoAddressService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KakaoAddressRepository
    @Inject
    constructor(private val service: KakaoAddressService) : AddressRepository {
        override suspend fun fetchAddresses(
            keyword: String,
            pageSize: Int,
        ): Flow<PagingData<Address>> {
            return Pager(
                config = PagingConfig(pageSize = pageSize),
                pagingSourceFactory = { AddressPagingSource(service, keyword, pageSize) },
            ).flow
        }

        override suspend fun fetchAddressesByCoordinate(
            x: String,
            y: String,
        ): ApiResult<String?> {
            return service.fetchAddressesByCoordinate(x, y).map {
                if (it.documents.isNotEmpty()) {
                    it.documents[0].address?.addressName
                } else {
                    null
                }
            }
        }
    }
