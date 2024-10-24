package com.mulberry.ody.presentation.setting.listener

import androidx.appcompat.widget.SwitchCompat
import com.mulberry.ody.presentation.setting.model.SettingItemType

interface SettingListener {
    fun onClickSettingItem(settingItemType: SettingItemType)

    fun onChangeSettingSwitchItem(
        switch: SwitchCompat,
        settingItemType: SettingItemType,
        isChecked: Boolean,
    )
}
