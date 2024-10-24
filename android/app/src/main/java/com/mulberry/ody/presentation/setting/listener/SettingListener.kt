package com.mulberry.ody.presentation.setting.listener

import com.mulberry.ody.presentation.setting.model.SettingItemType

interface SettingListener {
    fun onClickSettingItem(settingItemType: SettingItemType)

    fun onChangeSettingSwitchItem(
        settingItemType: SettingItemType,
        isChecked: Boolean,
    )
}
