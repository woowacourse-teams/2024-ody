package com.mulberry.ody.presentation.common.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.toApiResult
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class GeoLocationHelper(
    context: Context,
) : LocationHelper {
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    override suspend fun getCurrentCoordinate(): ApiResult<Location> {
        val currentLocationRequest =
            CurrentLocationRequest.Builder()
                .setDurationMillis(LOCATION_REQUEST_DURATION_MILLIS)
                .setMaxUpdateAgeMillis(LOCATION_REQUEST_MAX_AGE_MILLIS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

        return runCatching { getCurrentLocation(currentLocationRequest) }.toApiResult()
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(currentLocationRequest: CurrentLocationRequest): Location {
        return withTimeout(LOCATION_TIME_OUT_MILLIS) {
            val location = fusedLocationProviderClient.getCurrentLocation(
                currentLocationRequest,
                CancellationTokenSource().token,
            ).await()

            location ?: throw IllegalArgumentException(LOCATION_EXCEPTION_MESSAGE)
        }
    }

    companion object {
        private const val LOCATION_TIME_OUT_MILLIS = 3000L
        private const val LOCATION_REQUEST_DURATION_MILLIS = 30_000L
        private const val LOCATION_REQUEST_MAX_AGE_MILLIS = 60_000L
        private const val LOCATION_EXCEPTION_MESSAGE = "Location이 null 입니다."
    }
}
