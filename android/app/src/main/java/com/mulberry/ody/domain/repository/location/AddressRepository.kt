package com.mulberry.ody.domain.repository.location

import androidx.paging.PagingData
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun fetchAddress(
        keyword: String,
        pageSize: Int,
    ): Flow<PagingData<Address>>

    suspend fun fetchAddressNameByCoordinate(
        longitude: String,
        latitude: String,
    ): ApiResult<String?>
}
