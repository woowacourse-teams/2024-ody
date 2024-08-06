package com.woowacourse.ody.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.intro.IntroActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val application: OdyApplication by lazy {
        applicationContext as OdyApplication
    }
    private val viewModel: SplashViewModel by viewModels {
        SplashViewModelFactory(application.fcmTokenRepository, application.meetingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.makeTransparentStatusBar()
        initializeObserve()
    }

    private fun initializeObserve() {
        viewModel.navigateAction.observe(this) {
            val intent = IntroActivity.getIntent(this@SplashActivity)
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
