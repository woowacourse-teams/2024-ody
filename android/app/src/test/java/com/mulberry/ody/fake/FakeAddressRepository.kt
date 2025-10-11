package com.mulberry.ody.fake

import androidx.paging.PagingData
import com.mulberry.ody.addresses
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object FakeAddressRepository : AddressRepository {
    override fun fetchAddress(
        keyword: String,
        pageSize: Int,
    ): Flow<PagingData<Address>> {
        return flowOf(PagingData.from(addresses.addresses))
    }

    override suspend fun fetchAddressNameByCoordinate(
        x: String,
        y: String,
    ): ApiResult<String?> {
        return ApiResult.Success("서울시 강남구")
    }
}
