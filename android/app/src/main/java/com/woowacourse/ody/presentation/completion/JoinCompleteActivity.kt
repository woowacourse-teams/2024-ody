package com.woowacourse.ody.presentation.completion

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
            delay(1500)
            finish()
        }
    }
}
