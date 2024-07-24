package com.woowacourse.ody.presentation.completion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity
import com.woowacourse.ody.presentation.meetinglog.LogsActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class JoinCompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_complete)

        lifecycleScope.launch {
            delay(1500)
            finishAffinity()
            startActivity(LogsActivity.getIntent(this@JoinCompleteActivity))
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, JoinCompleteActivity::class.java)
    }
}
