package com.mulberry.ody.presentation.feature.setting.model

interface SettingAction {
    fun toggleNotificationDeparture(isChecked: Boolean)

    fun toggleNotificationEntry(isChecked: Boolean)

    fun logout()

    fun withdraw()
}
