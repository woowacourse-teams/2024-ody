package com.woowacourse.ody.presentation.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, SettingActivity::class.java)
    }
}