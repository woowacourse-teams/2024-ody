package com.woowacourse.ody.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.intro.IntroActivity
import com.woowacourse.ody.presentation.room.MeetingRoomActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val application: OdyApplication by lazy {
        applicationContext as OdyApplication
    }
    private val viewModel: SplashViewModel by viewModels {
        application.splashViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.makeTransparentStatusBar()
        initializeObserve()

        lifecycleScope.launch {
            application.fcmTokenRepository.fetchFCMToken().onSuccess {
                viewModel.fetchMeeting()
            }.onFailure {
                startActivity(IntroActivity.getIntent(this@SplashActivity))
            }
        }
    }

    private fun initializeObserve() {
        viewModel.meeting.observe(this) {
            val intent =
                if (it == null) {
                    IntroActivity.getIntent(this@SplashActivity)
                } else {
                    MeetingRoomActivity.getIntent(this@SplashActivity, it)
                }
            startActivity(intent)
            finish()
        }
    }
}

private fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}
