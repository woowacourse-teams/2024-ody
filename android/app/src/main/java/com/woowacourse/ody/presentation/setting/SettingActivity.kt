package com.woowacourse.ody.presentation.setting

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivitySettingBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.setting.adapter.SettingsAdapter
import com.woowacourse.ody.presentation.setting.listener.SettingListener
import com.woowacourse.ody.presentation.setting.model.Setting

class SettingActivity :
    BindingActivity<ActivitySettingBinding>(R.layout.activity_setting),
    BackListener,
    SettingListener {
    private val adapter by lazy { SettingsAdapter(this) }

    override fun initializeBinding() {
        binding.backListener = this
        binding.rvSetting.adapter = adapter

        val dividerItemDecoration =
            MaterialDividerItemDecoration(this, LinearLayout.VERTICAL).apply {
                isLastItemDecorated = false
                dividerInsetStart = dpToPx(26)
                dividerInsetEnd = dpToPx(26)
            }

        binding.rvSetting.addItemDecoration(dividerItemDecoration)

        adapter.submitList(Setting.entries)
    }

    override fun onBack() = finish()

    override fun onSetting(setting: Setting) {
        when (setting) {
            Setting.PRIVACY_POLICY -> {
            }

            Setting.TERM -> {
            }

            Setting.LOGOUT -> {
            }

            Setting.WITHDRAW -> {
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}
