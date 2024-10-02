package com.mulberry.ody.presentation.common.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await

class GeoLocationHelper(
    context: Context,
) : LocationHelper {
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentCoordinate(): Result<Location> {
        val currentLocationRequest =
            CurrentLocationRequest.Builder()
                .setDurationMillis(LOCATION_REQUEST_DURATION_MILLIS)
                .setMaxUpdateAgeMillis(LOCATION_REQUEST_MAX_AGE_MILLIS)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

        return runCatching {
            fusedLocationProviderClient.getCurrentLocation(
                currentLocationRequest,
                CancellationTokenSource().token,
            ).await() ?: throw IllegalArgumentException(LOCATION_EXCEPTION_MESSAGE)
        }
    }

    companion object {
        private const val LOCATION_REQUEST_DURATION_MILLIS = 30_000L
        private const val LOCATION_REQUEST_MAX_AGE_MILLIS = 60_000L
        private const val LOCATION_EXCEPTION_MESSAGE = "Location이 null 입니다."
    }
}
