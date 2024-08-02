package com.woowacourse.ody.presentation.join.complete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JoinCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_complete)

        lifecycleScope.launch {
            delay(FINISH_DELAY_MILLIS)
            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val FINISH_DELAY_MILLIS = 1500L

        fun getIntent(context: Context): Intent = Intent(context, JoinCompleteActivity::class.java)
    }
}
