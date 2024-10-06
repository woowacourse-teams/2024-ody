package com.mulberry.ody.presentation.common.gps

import android.location.Location

interface LocationHelper {
    suspend fun getCurrentCoordinate(): Result<Location>
}
