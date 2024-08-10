package com.woowacourse.ody.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class PermissionRequest(
    private val requestCode: Int,
) {
    private var _state: PermissionState = PermissionState.Denied
    val state get() = _state

    fun permit(grantResults: IntArray): PermissionRequest  {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            _state = PermissionState.Granted
        } else {
            _state = PermissionState.Denied
        }
        return this
    }
}

fun PermissionRequest.onGranted(func: () -> Unit): PermissionRequest {
    if (this.state is PermissionState.Granted) func()
    return this
}

fun PermissionRequest.onDenied(func: () -> Unit): PermissionRequest {
    if (this.state is PermissionState.Denied) func()
    return this
}

sealed class PermissionState() {
    data object Granted : PermissionState()

    data object Denied : PermissionState()
}

class PermissionHelper(
    private val context: Context,
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_REQUEST_CODE,
        )
    }

    fun requestCoarseAndFineLocationPermission(activity: Activity) {
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

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPermissions(activity: Activity) {
        if (hasNotificationPermission() &&
            hasFineLocationPermission() &&
            hasCoarseLocationPermission() &&
            hasBackgroundLocationPermission()
        ) {
            return
        }

        requestNotificationPermission(activity)
    }

    private fun hasLocationPermissions(): Boolean =
        hasFineLocationPermission() &&
            hasCoarseLocationPermission() &&
            hasBackgroundLocationPermission()

    fun onRequest() {
    }

    /*
     */

    companion object {
        const val NOTIFICATION_REQUEST_CODE = 1001
        const val COARSE_AND_FINE_LOCATION_REQUEST_CODE = 1002
        const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1003
    }
}
