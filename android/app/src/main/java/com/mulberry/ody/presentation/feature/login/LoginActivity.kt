package com.mulberry.ody.presentation.feature.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityLoginBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.feature.meetings.MeetingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        viewModel.verifyNavigation()
        viewModel.verifyLogin()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.navigatedReason) {
            when (it) {
                LoginNavigatedReason.LOGOUT -> {
                    showSnackBar(R.string.login_logout_success)
                }

                LoginNavigatedReason.WITHDRAWAL -> {
                    showSnackBar(R.string.login_withdrawal_success)
                }
            }
        }
        collectWhenStarted(viewModel.navigateAction) {
            val intent = MeetingsActivity.getIntent(this@LoginActivity)
            startActivity(intent)
            finish()
        }
        collectWhenStarted(viewModel.networkErrorEvent) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        collectWhenStarted(viewModel.errorEvent) {
            showSnackBar(R.string.error_guide)
        }
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
