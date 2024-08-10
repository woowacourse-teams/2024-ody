package com.woowacourse.ody.presentation.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R

enum class PermissionRequestType(val requestCode: Int, val deniedMessageId: Int) {
    NOTIFICATION(
        PermissionHelper.NOTIFICATION_REQUEST_CODE,
        R.string.meetings_notification_permission_required,
    ),
    LOCATION(
        PermissionHelper.COARSE_AND_FINE_LOCATION_REQUEST_CODE,
        R.string.meetings_location_permission_required,
    ),
    BACKGROUND_LOCATION(
        PermissionHelper.BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE,
        R.string.meetings_location_permission_required,
    ),
    ;

    companion object {
        fun of(requestCode: Int) = entries.find { it.requestCode == requestCode }
    }
}

enum class PermissionState {
    Granted,
    Denied,
}

class PermissionResult(
    requestCode: Int,
    grantResults: IntArray,
) {
    val permissionRequestType: PermissionRequestType? = PermissionRequestType.of(requestCode)
    val state =
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PermissionState.Granted
        } else {
            PermissionState.Denied
        }

    fun onGranted(func: (PermissionResult) -> Unit): PermissionResult {
        if (this.state == PermissionState.Granted) func(this)
        return this
    }

    fun onDenied(func: (PermissionResult) -> Unit): PermissionResult {
        if (this.state == PermissionState.Denied) func(this)
        return this
    }
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
