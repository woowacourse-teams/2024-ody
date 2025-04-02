package com.mulberry.ody.presentation.feature.setting

import androidx.compose.foundation.background
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.ErrorSnackbarHandler
import com.mulberry.ody.presentation.common.modifier.noRippleClickable
import com.mulberry.ody.presentation.component.OdySadDialog
import com.mulberry.ody.presentation.component.OdyTopAppBar
import com.mulberry.ody.presentation.feature.login.LoginNavigatedReason
import com.mulberry.ody.presentation.feature.setting.model.SettingAction
import com.mulberry.ody.presentation.feature.setting.model.SettingItemType
import com.mulberry.ody.presentation.feature.setting.model.SettingNavigation
import com.mulberry.ody.presentation.feature.setting.model.SettingNotificationItemType
import com.mulberry.ody.presentation.feature.setting.model.SettingServiceItemType
import com.mulberry.ody.presentation.feature.setting.model.SettingState
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.OdyTheme
import com.mulberry.ody.presentation.theme.White

@Composable
fun SettingScreen(
    onClickBack: () -> Unit,
    settingNavigation: SettingNavigation,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val settingAction =
        object : SettingAction {
            override fun toggleNotificationDeparture(isChecked: Boolean) = viewModel.changeDepartureNotification(isChecked)

            override fun toggleNotificationEntry(isChecked: Boolean) = viewModel.changeEntryNotification(isChecked)

            override fun logout() = viewModel.logout()

            override fun withdraw() = viewModel.withdraw()
        }

    val settingState =
        remember {
            SettingState(
                context = context,
                settingAction = settingAction,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState,
                settingNavigation = settingNavigation,
            )
        }

    val isNotificationDepartureOn by viewModel.isDepartureNotificationOn.collectAsStateWithLifecycle()
    val isNotificationEntryOn by viewModel.isEntryNotificationOn.collectAsStateWithLifecycle()

    val isSwitchOn: (SettingNotificationItemType) -> Boolean = { type ->
        when (type) {
            SettingNotificationItemType.NOTIFICATION_DEPARTURE -> isNotificationDepartureOn
            SettingNotificationItemType.NOTIFICATION_ENTRY -> isNotificationEntryOn
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = OdyTheme.colors.primary,
        topBar = {
            OdyTopAppBar(
                title = stringResource(id = R.string.setting_main_title),
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            tint = Color.Unspecified,
                            contentDescription = null,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        if (settingState.showWithdrawalDialog) {
            WithdrawalDialog(
                onClickWithdrawal = settingState::withdraw,
                onClickCancel = settingState::cancelWithdraw,
            )
        }
        SettingContent(
            onClickItem = settingState::onClickServiceItem,
            onChangedChecked = settingState::toggleNotificationItem,
            isSwitchOn = if (!settingState.hasNotificationPermission) { _ -> false } else isSwitchOn,
            modifier = Modifier.padding(innerPadding),
        )
    }

    ErrorSnackbarHandler(viewModel)
    LifecycleEventEffect(event = Lifecycle.Event.ON_START) {
        viewModel.fetchNotificationSetting()
        settingState.updatePermission()
    }
    LaunchedEffect(Unit) {
        viewModel.loginNavigateEvent.collect {
            when (it) {
                LoginNavigatedReason.LOGOUT -> settingNavigation.navigateToLoginByLogout()
                LoginNavigatedReason.WITHDRAWAL -> settingNavigation.navigateToLoginByWithdrawal()
            }
        }
    }
}

@Composable
private fun WithdrawalDialog(
    onClickWithdrawal: () -> Unit,
    onClickCancel: () -> Unit,
) {
    OdySadDialog(
        title = {
            Row {
                Text(
                    text = stringResource(id = R.string.setting_withdrawal_title_front),
                    style = OdyTheme.typography.pretendardBold24.copy(OdyTheme.colors.quinary),
                )
                Text(
                    text = stringResource(id = R.string.setting_withdrawal_title_middle),
                    style = OdyTheme.typography.pretendardBold24.copy(OdyTheme.colors.secondary),
                )
                Text(
                    text = stringResource(id = R.string.setting_withdrawal_title_back),
                    style = OdyTheme.typography.pretendardBold24.copy(OdyTheme.colors.quinary),
                )
            }
        },
        subtitle = {
            Text(
                text = stringResource(id = R.string.setting_withdrawal_subtitle),
                style = OdyTheme.typography.pretendardMedium16.copy(OdyTheme.colors.quinary),
            )
        },
        onClickCancel = onClickCancel,
        onClickConfirm = onClickWithdrawal,
        confirmButtonText = stringResource(id = R.string.setting_withdrawal_button),
    )
}

@Composable
private fun SettingContent(
    onClickItem: (SettingServiceItemType) -> Unit,
    onChangedChecked: (SettingNotificationItemType, Boolean) -> Unit,
    isSwitchOn: (SettingNotificationItemType) -> Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        SettingNotificationItems(
            onChangedChecked = onChangedChecked,
            isSwitchOn = isSwitchOn,
            modifier =
                Modifier
                    .padding(horizontal = 26.dp)
                    .padding(top = 26.dp),
        )
        HorizontalDivider(
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
    onChangedChecked: (SettingNotificationItemType, Boolean) -> Unit,
    isSwitchOn: (SettingNotificationItemType) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val items = SettingNotificationItemType.entries
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
        items.forEachIndexed { index, type ->
            SettingSwitchItem(
                type = type,
                isChecked = isSwitchOn(type),
                onChangedChecked = onChangedChecked,
            )
            if (index != items.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun SettingItems(
    onClickItem: (SettingServiceItemType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = SettingServiceItemType.entries
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.setting_header_service),
            style = OdyTheme.typography.pretendardBold18.copy(OdyTheme.colors.nonary),
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .padding(bottom = 28.dp),
        )
        items.forEachIndexed { index, type ->
            SettingItem(
                settingItemType = type,
                onClickItem = onClickItem,
            )
            if (index != items.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun SettingSwitchItem(
    type: SettingNotificationItemType,
    isChecked: Boolean = true,
    onChangedChecked: (SettingNotificationItemType, Boolean) -> Unit = { _, _ -> },
) {
    SettingItem(
        settingItemType = type,
        trailingIcon = {
            Switch(
                checked = isChecked,
                onCheckedChange = { isChecked -> onChangedChecked(type, isChecked) },
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
        },
    )
}

@Composable
private fun SettingItem(
    settingItemType: SettingItemType,
    onClickItem: (SettingServiceItemType) -> Unit = {},
    trailingIcon: (@Composable () -> Unit) = {},
) {
    Row(
        modifier =
            Modifier
                .height(34.dp)
                .noRippleClickable {
                    if (settingItemType is SettingServiceItemType) {
                        onClickItem(settingItemType)
                    }
                }
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = settingItemType.icon),
            tint = Color.Unspecified,
            contentDescription = null,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = stringResource(id = settingItemType.description),
            style = OdyTheme.typography.pretendardMedium18.copy(color = OdyTheme.colors.quinary),
            modifier = Modifier.weight(1f),
        )
        trailingIcon()
    }
}

@Composable
@Preview(showSystemUi = true)
private fun SettingContentPreview() {
    OdyTheme {
        SettingContent(
            onClickItem = {},
            onChangedChecked = { _, _ -> },
            isSwitchOn = { type ->
                when (type) {
                    SettingNotificationItemType.NOTIFICATION_DEPARTURE -> true
                    SettingNotificationItemType.NOTIFICATION_ENTRY -> false
                }
            },
        )
    }
}
