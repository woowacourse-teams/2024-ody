package com.ydo.ody.presentation.creation.complete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ydo.ody.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MeetingCompletionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_completion)

        lifecycleScope.launch {
            delay(FINISH_DELAY_MILLIS)
            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val FINISH_DELAY_MILLIS = 1500L

        fun getIntent(context: Context): Intent = Intent(context, MeetingCompletionActivity::class.java)
    }
}
