package com.mulberry.ody.presentation.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityInviteCodeBinding
import com.mulberry.ody.presentation.collectLifecycleFlow
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
        collectLifecycleFlow(viewModel.alreadyParticipatedEvent) {
            viewModel.clearInviteCode()
            showSnackBar(R.string.invite_code_already_participated)
        }
        collectLifecycleFlow(viewModel.invalidInviteCodeEvent) {
            viewModel.clearInviteCode()
            showSnackBar(R.string.invite_code_invalid_invite_code)
        }
        collectLifecycleFlow(viewModel.navigateAction) {
            navigateToJoinView()
            finish()
        }
        collectLifecycleFlow(viewModel.networkErrorEvent) {
            showRetrySnackBar {
                viewModel.retryLastAction()
            }
        }
        collectLifecycleFlow(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectLifecycleFlow
            }
            hideLoadingDialog()
        }
    }

    private fun navigateToJoinView() {
        val inviteCode = viewModel.inviteCode.value ?: return
        startActivity(MeetingJoinActivity.getIntent(inviteCode, this))
    }

    override fun onBack() = finish()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, InviteCodeActivity::class.java)
    }
}
