package com.mulberry.ody.presentation.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.setting.model.SettingItemType
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White

@Composable
fun SettingScreen(
    onClickBack: () -> Unit,
    onClickItem: (SettingItemType) -> Unit,
    onChangedChecked: (SettingItemType, Boolean) -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val notificationDepartureSwitchChecked by rememberSaveable { mutableStateOf(false) }
    val notificationEntrySwitchChecked by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = stringResource(id = R.string.setting_main_title),
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = OdyTheme.colors.tertiary,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        SettingContent(
            onClickItem = onClickItem,
            onChangedChecked = onChangedChecked,
            notificationDepartureSwitchChecked = notificationDepartureSwitchChecked,
            notificationEntrySwitchChecked = notificationEntrySwitchChecked,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun SettingContent(
    onClickItem: (SettingItemType) -> Unit,
    onChangedChecked: (SettingItemType, Boolean) -> Unit,
    notificationDepartureSwitchChecked: Boolean,
    notificationEntrySwitchChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        SettingNotificationItems(
            onChangedChecked = onChangedChecked,
            notificationDepartureSwitchChecked = notificationDepartureSwitchChecked,
            notificationEntrySwitchChecked = notificationEntrySwitchChecked,
            modifier =
                Modifier
                    .padding(horizontal = 26.dp)
                    .padding(top = 26.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                    .background(OdyTheme.colors.senary)
                    .height(3.dp),
        )
        SettingItems(
            onClickItem = onClickItem,
            modifier =
                Modifier
                    .padding(horizontal = 26.dp),
        )
    }
}

@Composable
private fun SettingNotificationItems(
    onChangedChecked: (SettingItemType, Boolean) -> Unit,
    notificationDepartureSwitchChecked: Boolean,
    notificationEntrySwitchChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.setting_header_notification),
            style = OdyTheme.typography.pretendardBold18.copy(OdyTheme.colors.nonary),
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .padding(bottom = 28.dp),
        )
        SettingItem(
            settingItemType = SettingItemType.NOTIFICATION_DEPARTURE,
            isChecked = notificationDepartureSwitchChecked,
            onChangedChecked = onChangedChecked,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        SettingItem(
            settingItemType = SettingItemType.NOTIFICATION_ENTRY,
            isChecked = notificationEntrySwitchChecked,
            onChangedChecked = onChangedChecked,
        )
    }
}

@Composable
private fun SettingItems(
    onClickItem: (SettingItemType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemType = SettingItemType.entries.filterNot { it.isSwitch }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.setting_header_service),
            style = OdyTheme.typography.pretendardBold18.copy(OdyTheme.colors.nonary),
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .padding(bottom = 28.dp),
        )
        itemType.forEachIndexed { index, type ->
            SettingItem(
                settingItemType = type,
                onClickItem = onClickItem,
            )
            if (index != itemType.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun SettingItem(
    settingItemType: SettingItemType,
    onClickItem: (SettingItemType) -> Unit = {},
    isChecked: Boolean = true,
    onChangedChecked: (SettingItemType, Boolean) -> Unit = { _, _ -> },
) {
    Row(
        modifier =
            Modifier
                .height(34.dp)
                .noRippleClickable { onClickItem(settingItemType) }
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = settingItemType.icon),
            tint = OdyTheme.colors.quarternary,
            contentDescription = null,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(id = settingItemType.description),
            style = OdyTheme.typography.pretendardMedium18.copy(color = OdyTheme.colors.quinary),
            modifier = Modifier.weight(1f),
        )
        if (settingItemType.isSwitch) {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked -> onChangedChecked(settingItemType, isChecked) },
                modifier =
                    Modifier
                        .requiredSize(40.dp)
                        .scale(0.8f),
                colors =
                    SwitchDefaults.colors(
                        checkedThumbColor = White,
                        checkedTrackColor = OdyTheme.colors.secondary,
                        checkedBorderColor = OdyTheme.colors.secondary,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = Gray400,
                        uncheckedBorderColor = Gray400,
                    ),
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
private fun SettingContentPreview() {
    OdyTheme {
        SettingContent(
            onClickItem = {},
            onChangedChecked = { _, _ -> },
            notificationDepartureSwitchChecked = true,
            notificationEntrySwitchChecked = false,
        )
    }
}
