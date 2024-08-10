package com.woowacourse.ody.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper(private val context: Context) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_REQUEST_CODE,
        )
    }

    fun coarseAndFineLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            COARSE_AND_FINE_LOCATION_REQUEST_CODE,
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestBackgroundLocationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ),
            BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE,
        )
    }

    fun hasCoarseLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasFineLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 1001
        const val COARSE_AND_FINE_LOCATION_REQUEST_CODE = 1002
        const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1003
    }
}
