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
import com.woowacourse.ody.data.remote.ody.repository.DefaultMeetingRepository
import com.woowacourse.ody.presentation.intro.IntroActivity
import com.woowacourse.ody.presentation.meetingroom.MeetingRoomActivity
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
        initializeObserve()

        lifecycleScope.launch {
            (application as OdyApplication).odyDatastore.getToken().collect {
                if (it.isEmpty()) {
                    startActivity(IntroActivity.getIntent(this@SplashActivity))
                } else {
                    viewModel.fetchMeeting()
                }
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
