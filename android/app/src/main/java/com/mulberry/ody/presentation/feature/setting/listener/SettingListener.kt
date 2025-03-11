package com.mulberry.ody.presentation.feature.setting.listener

import androidx.appcompat.widget.SwitchCompat
import com.mulberry.ody.presentation.feature.setting.model.SettingItemType

interface SettingListener {
    fun onClickSettingItem(settingItemType: SettingItemType)

    fun onInitializeSettingSwitchItem(
        switch: SwitchCompat,
        settingItemType: SettingItemType,
    )

    fun onChangeSettingSwitchItem(
        switch: SwitchCompat,
        settingItemType: SettingItemType,
        isChecked: Boolean,
    )
}
