package com.woowacourse.ody.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.core.repository.DefaultLoginRepository
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoOAuthLoginService
import com.woowacourse.ody.databinding.ActivityLoginBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by lazy {
        val kakaoOAuthLoginService = KakaoOAuthLoginService(this)
        val loginRepository =
            DefaultLoginRepository(
                application.loginService,
                kakaoOAuthLoginService,
                application.fcmTokenRepository,
            )
        LoginViewModelFactory(loginRepository).create(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.makeTransparentStatusBar()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}

private fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}
