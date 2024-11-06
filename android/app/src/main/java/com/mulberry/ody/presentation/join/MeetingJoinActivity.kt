package com.mulberry.ody.presentation.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingJoinBinding
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.address.AddressSearchFragment
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.collectWhenStarted
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.common.listener.NextListener
import com.mulberry.ody.presentation.join.complete.JoinCompleteActivity
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeetingJoinActivity :
    BindingActivity<ActivityMeetingJoinBinding>(R.layout.activity_meeting_join),
    NextListener,
    BackListener,
    AddressSearchListener {
    private val viewModel: MeetingJoinViewModel by viewModels<MeetingJoinViewModel>()
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }

    @Inject
    lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
        binding.nextListener = this
        binding.backListener = this
        binding.addressSearchListener = this
    }

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        collectWhenStarted(viewModel.invalidDepartureEvent) {
            showSnackBar(R.string.invalid_address)
        }
        collectWhenStarted(viewModel.navigateAction) {
            when (it) {
                is MeetingJoinNavigateAction.JoinNavigateToRoom -> {
                    navigateToNotificationRoom(it.meetingId)
                }

                MeetingJoinNavigateAction.JoinNavigateToJoinComplete -> {
                    startActivity(JoinCompleteActivity.getIntent(this@MeetingJoinActivity))
                }
            }
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
        collectWhenStarted(viewModel.defaultLocationError) {
            showSnackBar(R.string.default_location_error_guide)
        }
    }

    private fun navigateToNotificationRoom(meetingId: Long) {
        val intent =
            MeetingRoomActivity.getIntent(
                this,
                meetingId,
                MeetingRoomActivity.NAVIGATE_TO_NOTIFICATION_LOG,
            )
        startActivity(intent)
        finish()
    }

    override fun onNext() {
        viewModel.onClickMeetingJoin(getInviteCode())
    }

    override fun onBack() = finish()

    private fun getInviteCode(): String = intent.getStringExtra(INVITE_CODE_KEY) ?: ""

    override fun onSearch() {
        supportFragmentManager.commit {
            add(R.id.fcv_join, AddressSearchFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onReceive(address: Address) {
        viewModel.departureAddress.value = address
    }

    companion object {
        private const val INVITE_CODE_KEY = "invite_code_key"

        fun getIntent(
            inviteCode: String,
            context: Context,
        ): Intent {
            return Intent(context, MeetingJoinActivity::class.java).apply {
                putExtra(INVITE_CODE_KEY, inviteCode)
            }
        }
    }
}
