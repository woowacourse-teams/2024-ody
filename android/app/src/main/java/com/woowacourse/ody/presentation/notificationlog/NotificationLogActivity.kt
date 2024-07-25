package com.woowacourse.ody.presentation.notificationlog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.repository.DefaultMeetingRepository
import com.woowacourse.ody.data.remote.repository.DefaultNotificationLogRepository
import com.woowacourse.ody.databinding.ActivityNotificationLogBinding
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.presentation.notificationlog.adapter.NotificationLogListAdapter

class NotificationLogActivity : AppCompatActivity(), CopyInviteCodeListener, ShareListener {
    private val viewModel: NotificationLogViewModel by viewModels {
        NotificationLogViewModelFactory(
            DefaultNotificationLogRepository,
            DefaultMeetingRepository,
        )
    }

    private val binding: ActivityNotificationLogBinding by lazy {
        ActivityNotificationLogBinding.inflate(layoutInflater)
    }

    private val adapter: NotificationLogListAdapter by lazy {
        NotificationLogListAdapter(
            this,
        )
    }

    private val bottomSheetLayout by lazy { findViewById<ConstraintLayout>(R.id.cl_bottom_sheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
        initializePersistentBottomSheet()
        viewModel.initialize()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.rvNotificationLog.adapter = adapter
        binding.rvNotificationLog.layoutManager = LinearLayoutManager(this)
        binding.shareListener = this
        binding.copyInviteCodeListener = this
        setContentView(binding.root)
    }

    private fun initializeObserve() {
        viewModel.notificationLogs.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
    }

    override fun share() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun copyInviteCode() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val inviteCode = viewModel.meeting.value?.inviteCode
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(INVITE_CODE_LABEL, inviteCode)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        private const val INVITE_CODE_LABEL = "inviteCode"

        fun getIntent(
            context: Context,
            meeting: Meeting?,
        ): Intent = Intent(context, NotificationLogActivity::class.java)
    }
}
