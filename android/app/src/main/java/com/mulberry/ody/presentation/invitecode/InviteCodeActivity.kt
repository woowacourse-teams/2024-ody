package com.mulberry.ody.presentation.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityInviteCodeBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.join.MeetingJoinActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InviteCodeActivity : BindingActivity<ActivityInviteCodeBinding>(R.layout.activity_invite_code), BackListener {
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.alreadyParticipatedEvent.collect {
                        viewModel.clearInviteCode()
                        showSnackBar(R.string.invite_code_already_participated)
                    }
                }
                launch {
                    viewModel.invalidInviteCodeEvent.collect {
                        viewModel.clearInviteCode()
                        showSnackBar(R.string.invite_code_invalid_invite_code)
                    }
                }
                launch {
                    viewModel.navigateAction.collect {
                        navigateToJoinView()
                        finish()
                    }
                }
            }
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@observe
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
