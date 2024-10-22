package com.mulberry.ody.fake

import android.location.Location
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.presentation.common.gps.LocationHelper
import java.lang.IllegalArgumentException

object FakeLocationHelper : LocationHelper {
    override suspend fun getCurrentCoordinate(): ApiResult<Location> {
        return ApiResult.Unexpected(IllegalArgumentException())
    }
}
