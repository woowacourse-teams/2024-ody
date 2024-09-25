package com.mulberry.ody.domain.repository.location

import com.mulberry.ody.domain.model.Address

interface AddressRepository {
    suspend fun fetchAddresses(keyword: String): List<Address>
}
