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
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentCoordinate(): Location? {
        val currentLocationRequest =
            CurrentLocationRequest.Builder()
                .setDurationMillis(30_000L)
                .setMaxUpdateAgeMillis(60_000L)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

        return try {
            fusedLocationProviderClient.getCurrentLocation(
                currentLocationRequest,
                CancellationTokenSource().token,
            ).await()
        } catch (e: Exception) {
            null
        }
    }
}
