package com.ydo.ody.data.remote.thirdparty.location.repository

import com.ydo.ody.data.remote.thirdparty.location.entity.response.toGeoLocation
import com.ydo.ody.data.remote.thirdparty.location.service.KakaoLocationService
import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.apiresult.map
import com.ydo.ody.domain.model.GeoLocation
import com.ydo.ody.domain.repository.location.GeoLocationRepository

class KakaoGeoLocationRepository(private val service: KakaoLocationService) : GeoLocationRepository {
    override suspend fun fetchGeoLocation(address: String): ApiResult<GeoLocation> {
        return service.fetchLocation(address).map { it.toGeoLocation(address) }
    }
}
