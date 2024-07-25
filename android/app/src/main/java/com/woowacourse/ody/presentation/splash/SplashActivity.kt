package com.woowacourse.ody.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.repository.DefaultMeetingRepository
import com.woowacourse.ody.presentation.intro.IntroActivity
import com.woowacourse.ody.presentation.notificationlog.NotificationLogActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    val viewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(DefaultMeetingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.makeTransparentStatusBar()
        viewModel.initialize()
        initializeObserve()
    }

    private fun initializeObserve() {
        viewModel.meeting.observe(this) {
            lifecycleScope.launch {
                delay(1500)
                val intent =
                    if (it == null) {
                        IntroActivity.getIntent(this@SplashActivity)
                    } else {
                        NotificationLogActivity.getIntent(this@SplashActivity, it)
                    }
                startActivity(intent)
                finish()
            }
        }
    }
}

private fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}
