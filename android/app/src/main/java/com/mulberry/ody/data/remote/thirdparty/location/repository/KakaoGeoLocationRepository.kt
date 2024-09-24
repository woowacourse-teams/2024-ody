package com.mulberry.ody.data.remote.thirdparty.location.repository

import com.mulberry.ody.data.remote.thirdparty.location.entity.response.toGeoLocation
import com.mulberry.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.GeoLocation
import com.mulberry.ody.domain.repository.location.GeoLocationRepository
import javax.inject.Inject

class KakaoGeoLocationRepository
    @Inject
    constructor(private val service: KakaoLocationService) : GeoLocationRepository {
        override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
            return service.fetchLocation(address).map { it.toGeoLocation(address) }
        }
    }
