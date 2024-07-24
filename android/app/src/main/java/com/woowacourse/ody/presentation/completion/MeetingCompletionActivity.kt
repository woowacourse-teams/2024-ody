package com.woowacourse.ody.presentation.completion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MeetingCompletionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_completion)

        lifecycleScope.launch {
            delay(1500)
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingCompletionActivity::class.java)
    }
}
