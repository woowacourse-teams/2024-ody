package com.mulberry.ody.domain.usecase

import androidx.paging.PagingData
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchAddressUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
) {
    suspend operator fun invoke(keyword: String): Flow<PagingData<Address>> {
        return addressRepository.fetchAddresses(keyword, PAGE_SIZE)
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
