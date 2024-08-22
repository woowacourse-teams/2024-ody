package com.woowacourse.ody.presentation.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingJoinBinding
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog
import com.woowacourse.ody.presentation.address.listener.AddressSearchListener
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.common.listener.NextListener
import com.woowacourse.ody.presentation.join.complete.JoinCompleteActivity
import com.woowacourse.ody.presentation.room.MeetingRoomActivity

class MeetingJoinActivity :
    BindingActivity<ActivityMeetingJoinBinding>(R.layout.activity_meeting_join),
    NextListener,
    BackListener,
    AddressSearchListener {
    private val viewModel: MeetingJoinViewModel by viewModels<MeetingJoinViewModel> {
        MeetingJoinViewModelFactory(
            application.analyticsHelper,
            getInviteCode(),
            application.joinRepository,
            application.matesEtaRepository,
        )
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }

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
        viewModel.invalidDepartureEvent.observe(this) {
            showSnackBar(R.string.invalid_address)
        }
        viewModel.navigateAction.observe(this) {
            when (it) {
                is MeetingJoinNavigateAction.JoinNavigateToRoom -> {
                    navigateToNotificationRoom(it.meetingId)
                }

                MeetingJoinNavigateAction.JoinNavigateToJoinComplete -> {
                    startActivity(JoinCompleteActivity.getIntent(this))
                }
            }
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        viewModel.errorEvent.observe(this) {
            showSnackBar(R.string.error_guide)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@observe
            }
            hideLoadingDialog()
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
        viewModel.joinMeeting()
        viewModel.onClickMeetingJoin()
    }

    override fun onBack() = finish()

    private fun getInviteCode(): String = intent.getStringExtra(INVITE_CODE_KEY) ?: ""

    override fun onSearch() = AddressSearchDialog().show(supportFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)

    override fun onReceive(geoLocation: GeoLocation) {
        binding.etDeparture.setText(geoLocation.address)
        viewModel.departureGeoLocation.value = geoLocation
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
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
