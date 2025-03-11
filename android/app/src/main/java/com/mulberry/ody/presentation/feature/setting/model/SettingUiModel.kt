package com.mulberry.ody.presentation.feature.setting.model

import androidx.annotation.StringRes
import com.mulberry.ody.presentation.feature.setting.model.SettingUiModel.Companion.SETTING_DIVIDER_VIEW_TYPE
import com.mulberry.ody.presentation.feature.setting.model.SettingUiModel.Companion.SETTING_HEADER_VIEW_TYPE
import com.mulberry.ody.presentation.feature.setting.model.SettingUiModel.Companion.SETTING_ITEM_VIEW_TYPE

sealed interface SettingUiModel {
    val viewType: Int

    companion object {
        const val SETTING_HEADER_VIEW_TYPE = 0
        const val SETTING_ITEM_VIEW_TYPE = 1
        const val SETTING_DIVIDER_VIEW_TYPE = 2
    }
}

class SettingHeader(
    @StringRes val message: Int,
) : SettingUiModel {
    override val viewType: Int = SETTING_HEADER_VIEW_TYPE
}

class SettingItem(
    val type: SettingItemType,
    val isEnd: Boolean = false,
) : SettingUiModel {
    override val viewType: Int = SETTING_ITEM_VIEW_TYPE
}

class SettingDivider : SettingUiModel {
    override val viewType: Int = SETTING_DIVIDER_VIEW_TYPE
}
