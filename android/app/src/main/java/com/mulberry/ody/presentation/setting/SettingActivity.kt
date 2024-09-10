package com.mulberry.ody.presentation.setting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.mulberry.ody.BuildConfig
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivitySettingBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.login.LoginActivity
import com.mulberry.ody.presentation.setting.adapter.SettingsAdapter
import com.mulberry.ody.presentation.setting.listener.SettingListener
import com.mulberry.ody.presentation.setting.model.SettingUiModel
import com.mulberry.ody.presentation.setting.withdrawal.WithDrawalDialog

class SettingActivity :
    BindingActivity<ActivitySettingBinding>(R.layout.activity_setting),
    BackListener,
    SettingListener {
    private val adapter by lazy { SettingsAdapter(this) }
    private val viewModel by viewModels<SettingViewModel> {
        SettingViewModelFactory(
            analyticsHelper,
            application.authTokenRepository,
            application.kakaoLoginRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSettingAdapter()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.backListener = this
        binding.rvSetting.adapter = adapter
    }

    private fun initializeSettingAdapter() {
        val dividerItemDecoration =
            MaterialDividerItemDecoration(this, LinearLayout.VERTICAL).apply {
                isLastItemDecorated = false
                dividerInsetStart = dpToPx(SETTING_ITEM_HORIZONTAL_MARGIN_DP)
                dividerInsetEnd = dpToPx(SETTING_ITEM_HORIZONTAL_MARGIN_DP)
            }
        binding.rvSetting.addItemDecoration(dividerItemDecoration)
        adapter.submitList(SettingUiModel.entries)
    }

    private fun initializeObserve() {
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@observe
            }
            hideLoadingDialog()
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.errorEvent.observe(this) {
            showSnackBar(R.string.error_guide)
        }
        viewModel.isSuccessWithdrawal.observe(this) {
            startActivity(LoginActivity.getIntent(this))
            finishAffinity()
        }
    }

    override fun onBack() = finish()

    override fun onClickSettingItem(settingUiModel: SettingUiModel) {
        when (settingUiModel) {
            SettingUiModel.PRIVACY_POLICY -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PRIVACY_POLICY_URI))
                startActivity(intent)
            }

            SettingUiModel.TERM -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.TERM_URI))
                startActivity(intent)
            }

            SettingUiModel.LOGOUT -> {
            }

            SettingUiModel.WITHDRAW -> {
                WithDrawalDialog().show(supportFragmentManager, WITHDRAWAL_DIALOG_TAG)
            }
        }
    }

    private fun showWithDrawalDialog() {
        val image = AppCompatResources.getDrawable(this@SettingActivity, R.drawable.ic_sad_ody)
        val imageView = ImageView(this).apply { setImageDrawable(image) }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(imageView)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("이미지 다이얼로그")
            .setView(layout)
            .setPositiveButton("확인") { dialog, _ ->
                Log.e("TEST", "g화긴")
            }
            .setNegativeButton("취소") { dialog, _ ->
                Log.e("TEST", "c취소")
            }
            .create()

        dialog.show()
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    companion object {
        private const val SETTING_ITEM_HORIZONTAL_MARGIN_DP = 26
        private const val WITHDRAWAL_DIALOG_TAG = "withdrawal_dialog"

        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
