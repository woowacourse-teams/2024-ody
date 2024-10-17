package com.mulberry.ody.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityLoginBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.launchWhenStarted
import com.mulberry.ody.presentation.meetings.MeetingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        viewModel.checkIfNavigated()
        viewModel.checkIfLoggedIn()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        launchWhenStarted {
            launch {
                viewModel.navigatedReason.collect {
                    when (it) {
                        LoginNavigatedReason.LOGOUT -> {
                            showSnackBar(R.string.login_logout_success)
                        }

                        LoginNavigatedReason.WITHDRAWAL -> {
                            showSnackBar(R.string.login_withdrawal_success)
                        }
                    }
                }
            }
            launch {
                viewModel.navigateAction.collect {
                    val intent = MeetingsActivity.getIntent(this@LoginActivity)
                    startActivity(intent)
                    finish()
                }
            }
            launch {
                viewModel.networkErrorEvent.collect {
                    showRetrySnackBar { viewModel.retryLastAction() }
                }
            }
            launch {
                viewModel.errorEvent.collect {
                    showSnackBar(R.string.error_guide)
                }
            }
            launch {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        showLoadingDialog()
                        return@collect
                    }
                    hideLoadingDialog()
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
