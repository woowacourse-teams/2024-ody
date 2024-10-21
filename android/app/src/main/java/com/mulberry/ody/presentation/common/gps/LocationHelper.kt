package com.mulberry.ody.presentation.common.gps

import android.location.Location
import com.mulberry.ody.domain.apiresult.ApiResult

interface LocationHelper {
    suspend fun getCurrentCoordinate(): ApiResult<Location>
}
