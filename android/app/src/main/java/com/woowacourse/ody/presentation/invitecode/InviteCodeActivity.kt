package com.woowacourse.ody.presentation.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.core.repository.DefaultMeetingRepository
import com.woowacourse.ody.databinding.ActivityInviteCodeBinding
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.join.MeetingJoinActivity

class InviteCodeActivity : AppCompatActivity(), BackListener {
    private val binding: ActivityInviteCodeBinding by lazy {
        ActivityInviteCodeBinding.inflate(layoutInflater)
    }
    private val viewModel: InviteCodeViewModel by viewModels<InviteCodeViewModel> {
        InviteCodeViewModelFactory(DefaultMeetingRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    private fun initializeBinding() {
        setContentView(binding.root)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.backListener = this
    }

    private fun initializeObserve() {
        viewModel.isValidInviteCode.observe(this) { isValid ->
            if (isValid) {
                // 모임에 참여됨. 로그 화면으로 이동
                return@observe
            }
            viewModel.emptyInviteCode()
            showSnackBar(R.string.invalid_invite_code)
        }
        viewModel.navigateAction.observe(this) {
            navigateToJoinView()
        }
    }

    private fun showSnackBar(
        @StringRes messageId: Int,
    ) = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()

    private fun navigateToJoinView() {
        val inviteCode = viewModel.inviteCode.value ?: return
        startActivity(MeetingJoinActivity.getIntent(inviteCode, this))
    }

    override fun onBack() = finish()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, InviteCodeActivity::class.java)
    }
}
