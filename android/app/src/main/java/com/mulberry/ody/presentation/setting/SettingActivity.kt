package com.mulberry.ody.presentation.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivitySettingBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.setting.adapter.SettingsAdapter
import com.mulberry.ody.presentation.setting.listener.SettingListener
import com.mulberry.ody.presentation.setting.model.SettingUiModel

class SettingActivity :
    BindingActivity<ActivitySettingBinding>(R.layout.activity_setting),
    BackListener,
    SettingListener {
    private val adapter by lazy { SettingsAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSettingAdapter()
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

    override fun onBack() = finish()

    override fun onClickSettingItem(settingUiModel: SettingUiModel) {
        when (settingUiModel) {
            SettingUiModel.PRIVACY_POLICY -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URI))
                startActivity(intent)
            }

            SettingUiModel.TERM -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TERM_URI))
                startActivity(intent)
            }

            SettingUiModel.LOGOUT -> {
            }

            SettingUiModel.WITHDRAW -> {
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    companion object {
        private const val SETTING_ITEM_HORIZONTAL_MARGIN_DP = 26
        private const val PRIVACY_POLICY_URI =
            "https://sly-face-106.notion.site/fecbe589eb23471ba2d0685cb3c2d274?pvs=4"
        private const val TERM_URI =
            "https://sly-face-106.notion.site/beb204b6e6724ecbbb83496448fc7b53?pvs=4"

        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
