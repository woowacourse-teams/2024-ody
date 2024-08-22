package com.woowacourse.ody.presentation.login

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityLoginBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.meetings.MeetingsActivity

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            application.authTokenRepository,
            application.kakaoLoginRepository,
        )
    }

    private lateinit var splashScreen: SplashScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen()
        startSplash()
        super.onCreate(savedInstanceState)
        initializeObserve()
        viewModel.checkIfLogined()
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

    override fun initializeBinding() {
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        viewModel.navigateAction.observe(this) {
            val intent = MeetingsActivity.getIntent(this@LoginActivity)
            startActivity(intent)
            finish()
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.errorEvent.observe(this) {
            showSnackBar(R.string.error_guide)
        }
    }

    companion object {
        private const val SPLASH_DELAY_DURATION = 1500L

        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
