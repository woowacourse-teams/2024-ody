package com.mulberry.ody.presentation.feature.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import com.mulberry.ody.BuildConfig
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.databinding.ActivitySettingBinding
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.feature.login.LoginActivity
import com.mulberry.ody.presentation.feature.login.LoginNavigatedReason
import com.mulberry.ody.presentation.feature.setting.adapter.SettingsAdapter
import com.mulberry.ody.presentation.feature.setting.listener.SettingListener
import com.mulberry.ody.presentation.feature.setting.model.SettingDivider
import com.mulberry.ody.presentation.feature.setting.model.SettingHeader
import com.mulberry.ody.presentation.feature.setting.model.SettingItem
import com.mulberry.ody.presentation.feature.setting.model.SettingItemType
import com.mulberry.ody.presentation.feature.setting.model.SettingUiModel
import com.mulberry.ody.presentation.feature.setting.withdrawal.WithDrawalDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity :
    BindingActivity<ActivitySettingBinding>(R.layout.activity_setting),
    BackListener,
    SettingListener {
    private val adapter by lazy { SettingsAdapter(SETTINGS, this) }
    private val viewModel by viewModels<SettingViewModel>()

    @Inject
    lateinit var permissionHelper: PermissionHelper

    @Inject
    lateinit var odyDatastore: OdyDatastore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSettingAdapter()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.backListener = this
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.loginNavigateEvent) {
            when (it) {
                LoginNavigatedReason.LOGOUT -> {
                    navigateToLogin()
                }

                LoginNavigatedReason.WITHDRAWAL -> {
                    navigateToWithdrawal()
                }
            }
        }
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
        collectWhenStarted(viewModel.networkErrorEvent) {
            viewModel.networkErrorEvent.collect {
                showRetrySnackBar { viewModel.retryLastAction() }
            }
        }
        collectWhenStarted(viewModel.errorEvent) {
            showSnackBar(R.string.error_guide)
        }
    }

    private fun navigateToLogin() {
        val intent =
            LoginActivity.getIntent(this).apply {
                putExtra(NAVIGATED_REASON, LoginNavigatedReason.LOGOUT)
            }
        startActivity(intent)
        finishAffinity()
    }

    private fun navigateToWithdrawal() {
        val intent =
            LoginActivity.getIntent(this).apply {
                putExtra(NAVIGATED_REASON, LoginNavigatedReason.WITHDRAWAL)
            }
        startActivity(intent)
        finishAffinity()
    }

    private fun initializeSettingAdapter() {
        binding.rvSetting.adapter = adapter
    }

    override fun onBack() = finish()

    override fun onClickSettingItem(settingItemType: SettingItemType) {
        when (settingItemType) {
            SettingItemType.PRIVACY_POLICY -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_POLICY_URI))
                startActivity(intent)
            }

            SettingItemType.TERM -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TERM_URI))
                startActivity(intent)
            }

            SettingItemType.LOGOUT -> {
                viewModel.logout()
            }

            SettingItemType.WITHDRAW -> {
                WithDrawalDialog().show(supportFragmentManager, WITHDRAWAL_DIALOG_TAG)
            }

            SettingItemType.NOTIFICATION_DEPARTURE, SettingItemType.NOTIFICATION_ENTRY -> return
        }
    }

    override fun onInitializeSettingSwitchItem(
        switch: SwitchCompat,
        settingItemType: SettingItemType,
    ) {
        if (!permissionHelper.hasNotificationPermission()) {
            switch.isChecked = false
            return
        }

        val isNotificationOn =
            when (settingItemType) {
                SettingItemType.NOTIFICATION_DEPARTURE ->
                    odyDatastore.getIsNotificationOn(
                        NotificationType.DEPARTURE_REMINDER,
                    )

                SettingItemType.NOTIFICATION_ENTRY ->
                    odyDatastore.getIsNotificationOn(
                        NotificationType.ENTRY,
                    )

                SettingItemType.PRIVACY_POLICY, SettingItemType.TERM, SettingItemType.LOGOUT, SettingItemType.WITHDRAW -> return
            }
        lifecycleScope.launch {
            switch.isChecked = isNotificationOn.first()
        }
    }

    override fun onChangeSettingSwitchItem(
        switch: SwitchCompat,
        settingItemType: SettingItemType,
        isChecked: Boolean,
    ) {
        if (isChecked && !permissionHelper.hasNotificationPermission()) {
            switch.isChecked = false
            showSnackBarNotificationSettingAction()
            return
        }
        when (settingItemType) {
            SettingItemType.NOTIFICATION_DEPARTURE -> {
                lifecycleScope.launch {
                    odyDatastore.setIsNotificationOn(NotificationType.DEPARTURE_REMINDER, isChecked)
                }
            }

            SettingItemType.NOTIFICATION_ENTRY -> {
                lifecycleScope.launch {
                    odyDatastore.setIsNotificationOn(NotificationType.ENTRY, isChecked)
                }
            }

            SettingItemType.PRIVACY_POLICY, SettingItemType.TERM, SettingItemType.LOGOUT, SettingItemType.WITHDRAW -> return
        }
    }

    private fun showSnackBarNotificationSettingAction() {
        showSnackBar(
            R.string.setting_notification_permission_denied,
            R.string.setting_notification_permission_guide,
        ) {
            val intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, this@SettingActivity.packageName)
            startActivity(intent)
        }
    }

    companion object {
        private val SETTINGS: List<SettingUiModel> =
            listOf(
                SettingHeader(R.string.setting_header_notification),
                SettingItem(SettingItemType.NOTIFICATION_DEPARTURE),
                SettingItem(SettingItemType.NOTIFICATION_ENTRY, isEnd = true),
                SettingDivider(),
                SettingHeader(R.string.setting_header_service),
                SettingItem(SettingItemType.PRIVACY_POLICY),
                SettingItem(SettingItemType.TERM),
                SettingItem(SettingItemType.LOGOUT),
                SettingItem(SettingItemType.WITHDRAW, isEnd = true),
            )

        private const val NAVIGATED_REASON = "NAVIGATED_REASON"
        private const val WITHDRAWAL_DIALOG_TAG = "withdrawal_dialog"

        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
