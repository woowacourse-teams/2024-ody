package com.mulberry.ody.presentation.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityInviteCodeBinding
import com.mulberry.ody.presentation.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.join.MeetingJoinActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteCodeActivity :
    BindingActivity<ActivityInviteCodeBinding>(R.layout.activity_invite_code), BackListener {
    private val viewModel: InviteCodeViewModel by viewModels<InviteCodeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
        binding.backListener = this
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.invalidCodeEvent) { errorMmessage ->
            viewModel.clearInviteCode()
            showSnackBar(errorMmessage)
        }
        collectWhenStarted(viewModel.navigateAction) {
            navigateToJoinView()
            finish()
        }
        collectWhenStarted(viewModel.networkErrorEvent) {
            showRetrySnackBar {
                viewModel.retryLastAction()
            }
        }
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
    }

    private fun navigateToJoinView() {
        val inviteCode = viewModel.inviteCode.value
        startActivity(MeetingJoinActivity.getIntent(inviteCode, this))
    }

    override fun onBack() = finish()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, InviteCodeActivity::class.java)
    }
}
