package com.mulberry.ody.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityLoginBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.meetings.MeetingsActivity

class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(
            application.authTokenRepository,
            application.kakaoLoginRepository,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        viewModel.checkIfNavigated()
        viewModel.checkIfLogined()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        viewModel.navigatedReason.observe(this) {
            when (it) {
                LoginNavigatedReason.LOGOUT -> {
                    showSnackBar(R.string.logout_success)
                }
                LoginNavigatedReason.WITHDRAWAL -> {
                    showSnackBar(R.string.withdrawal_success)
                }
            }
        }
        viewModel.navigateAction.observe(this) {
            val intent = MeetingsActivity.getIntent(this@LoginActivity)
            startActivity(intent)
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.errorEvent.observe(this) {
            showSnackBar(R.string.error_guide)
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
