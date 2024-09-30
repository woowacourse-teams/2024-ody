package com.mulberry.ody.fake

import android.location.Location
import com.mulberry.ody.presentation.common.gps.LocationHelper

object FakeLocationHelper : LocationHelper {
    override suspend fun getCurrentCoordinate(): Result<Location> {
        return Result.failure(Exception())
    }
}
