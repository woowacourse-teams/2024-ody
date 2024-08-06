package com.woowacourse.ody.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.home.HomeActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.makeTransparentStatusBar()
        initializeObserve()
        viewModel.navigateToHome()
    }

    private fun initializeObserve() {
        viewModel.navigateAction.observe(this) {
            val intent = HomeActivity.getIntent(this@SplashActivity)
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
