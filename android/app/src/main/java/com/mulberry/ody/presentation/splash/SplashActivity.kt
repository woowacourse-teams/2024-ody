package com.mulberry.ody.presentation.splash

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mulberry.ody.presentation.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        startSplash()
        super.onCreate(savedInstanceState)
        navigateToLogin()
    }

    private fun startSplash() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView).run {
                duration = SPLASH_DELAY_DURATION
                doOnEnd {
                    splashScreenView.remove()
                }
                start()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = LoginActivity.getIntent(this)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val SPLASH_DELAY_DURATION = 1500L
    }
}
