package com.mulberry.ody.presentation.feature.setting.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.mulberry.ody.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SettingState(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val snackbarHostState: SnackbarHostState,
    private val settingAction: SettingAction,
    private val settingNavigation: SettingNavigation,
) {
    var showWithdrawalDialog by mutableStateOf(false)

    var hasNotificationPermission by mutableStateOf(true)
        private set

    fun onClickServiceItem(type: SettingServiceItemType) {
        when (type) {
            SettingServiceItemType.PRIVACY_POLICY -> settingNavigation.navigateToPrivacyPolicy()
            SettingServiceItemType.TERM -> settingNavigation.navigateToTerm()
            SettingServiceItemType.LOGOUT -> settingAction.logout()
            SettingServiceItemType.WITHDRAW -> showWithdrawalDialog = true
        }
    }

    fun toggleNotificationItem(
        type: SettingNotificationItemType,
        isChecked: Boolean,
    ) {
        if (!hasNotificationPermission) {
            showPermissionDeniedSnackbar()
            return
        }

        when (type) {
            SettingNotificationItemType.NOTIFICATION_DEPARTURE -> {
                settingAction.toggleNotificationDeparture(isChecked)
            }

            SettingNotificationItemType.NOTIFICATION_ENTRY -> {
                settingAction.toggleNotificationEntry(isChecked)
            }
        }
    }

    fun updatePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission = false
            return
        }

        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        hasNotificationPermission = result == PackageManager.PERMISSION_GRANTED
    }

    private fun showPermissionDeniedSnackbar() {
        coroutineScope.launch {
            val result =
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.setting_notification_permission_denied),
                    actionLabel = context.getString(R.string.setting_notification_permission_guide),
                    duration = SnackbarDuration.Short,
                )
            if (result == SnackbarResult.ActionPerformed) {
                settingNavigation.navigateToNotificationSetting()
            }
        }
    }

    fun withdraw() {
        settingAction.withdraw()
        showWithdrawalDialog = false
    }

    fun cancelWithdraw() {
        showWithdrawalDialog = false
    }
}
