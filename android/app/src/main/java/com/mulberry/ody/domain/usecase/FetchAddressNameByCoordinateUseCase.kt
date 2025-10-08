package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.repository.location.AddressRepository
import javax.inject.Inject

class FetchAddressNameByCoordinateUseCase @Inject constructor(
    private val addressRepository: AddressRepository,
){
    suspend operator fun invoke(
        longitude: String,
        latitude: String,
    ): ApiResult<String?> {
        return addressRepository.fetchAddressNameByCoordinate(longitude, latitude)
    }
}
