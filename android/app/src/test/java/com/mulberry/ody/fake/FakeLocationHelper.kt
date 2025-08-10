package com.mulberry.ody.fake

import android.location.Location
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.presentation.common.gps.LocationHelper
import java.lang.IllegalArgumentException

class FakeLocationHelper(private val location: Location?) : LocationHelper {
    override suspend fun getCurrentCoordinate(): ApiResult<Location> {
        return if (location == null) {
            ApiResult.Unexpected(IllegalArgumentException())
        } else {
            ApiResult.Success(location)
        }
    }
}
