package com.woowacourse.ody.presentation.invitecode

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.repository.DefaultMeetingRepository
import com.woowacourse.ody.databinding.ActivityInviteCodeBinding
import com.woowacourse.ody.presentation.meetinginfo.BackListener
import com.woowacourse.ody.presentation.notificationlog.NotificationLogActivity
import com.woowacourse.ody.util.observeEvent

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
        viewModel.isValidInviteCode.observeEvent(this) { isValid ->
            if (isValid) {
                // 모임에 참여됨. 로그 화면으로 이동
                return@observeEvent
            }
            viewModel.emptyInviteCode()
            showSnackBar(R.string.invalid_invite_code)
        }
        viewModel.navigateAction.observeEvent(this) {
            navigateToNotificationLog()
        }
    }

    private fun showSnackBar(
        @StringRes messageId: Int,
    ) = Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()

    private fun navigateToNotificationLog() {
        val intent = NotificationLogActivity.getIntent(this)
        startActivity(intent)
    }

    override fun onBack() = finish()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, InviteCodeActivity::class.java)
    }
}
