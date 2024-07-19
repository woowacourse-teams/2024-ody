package com.woowacourse.ody.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.woowacourse.ody.R
import com.woowacourse.ody.presentation.IntroActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.makeTransparentStatusBar()

        lifecycleScope.launch {
            delay(1500)
            startActivity(IntroActivity.getIntent(this@SplashActivity))
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
