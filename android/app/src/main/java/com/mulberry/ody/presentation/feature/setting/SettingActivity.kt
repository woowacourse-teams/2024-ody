package com.mulberry.ody.presentation.feature.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mulberry.ody.BuildConfig
import com.mulberry.ody.presentation.feature.login.LoginActivity
import com.mulberry.ody.presentation.feature.login.LoginNavigatedReason
import com.mulberry.ody.presentation.feature.setting.model.SettingNavigation
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : ComponentActivity(), SettingNavigation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OdyTheme {
                SettingScreen(
                    onBack = { finish() },
                    settingNavigation = this,
                )
            }
        }
    }

    override fun navigateToLoginByLogout() {
        val intent =
            LoginActivity.getIntent(this)
                .putExtra(NAVIGATED_REASON, LoginNavigatedReason.LOGOUT)
        startActivity(intent)
        finishAffinity()
    }

    override fun navigateToLoginByWithdrawal() {
        val intent =
            LoginActivity.getIntent(this)
                .putExtra(NAVIGATED_REASON, LoginNavigatedReason.WITHDRAWAL)
        startActivity(intent)
        finishAffinity()
    }

    override fun navigateToPrivacyPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_POLICY_URI))
        startActivity(intent)
    }

    override fun navigateToTerm() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TERM_URI))
        startActivity(intent)
    }

    override fun navigateToNotificationSetting() {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, this@SettingActivity.packageName)
        startActivity(intent)
    }

    companion object {
        private const val NAVIGATED_REASON = "NAVIGATED_REASON"

        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
