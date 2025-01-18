package com.mulberry.ody.presentation.room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingRoomBinding
import com.mulberry.ody.presentation.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.room.detail.DetailMeetingFragment
import com.mulberry.ody.presentation.room.etadashboard.EtaDashboardFragment
import com.mulberry.ody.presentation.room.listener.MeetingRoomListener
import com.mulberry.ody.presentation.room.log.ExitMeetingRoomDialog
import com.mulberry.ody.presentation.room.log.NotificationLogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeetingRoomActivity :
    BindingActivity<ActivityMeetingRoomBinding>(R.layout.activity_meeting_room),
    BackListener,
    MeetingRoomListener {
    @Inject
    lateinit var viewModelFactory: MeetingRoomViewModel.MeetingViewModelFactory

    private val viewModel: MeetingRoomViewModel by viewModels<MeetingRoomViewModel> {
        MeetingRoomViewModel.provideFactory(viewModelFactory, this, meetingId = getMeetingId())
    }

    private val fragments: Map<String, Fragment> by lazy {
        mapOf(
            NAVIGATE_TO_DETAIL_MEETING to DetailMeetingFragment(),
            NAVIGATE_TO_ETA_DASHBOARD to EtaDashboardFragment(),
            NAVIGATE_TO_NOTIFICATION_LOG to NotificationLogFragment(),
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
        if (savedInstanceState == null) {
            initializeFragment()
        }
    }

    override fun initializeBinding() = Unit

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        collectWhenStarted(viewModel.navigateToEtaDashboardEvent) {
            addFragment(EtaDashboardFragment())
        }
        collectWhenStarted(viewModel.networkErrorEvent) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        collectWhenStarted(viewModel.errorEvent) {
            showSnackBar(R.string.error_guide)
        }
        collectWhenStarted(viewModel.expiredNudgeTimeLimit) {
            showSnackBar(R.string.nudge_time_limit_expired)
        }
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
    }

    private fun initializeFragment() {
        val fragment = fragments[getNavigateView()] ?: return
        supportFragmentManager.commit {
            replace(R.id.fcv_meeting_room, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            add(R.id.fcv_meeting_room, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onBack() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun getMeetingId(): Long = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)

    private fun getNavigateView(): String = intent.getStringExtra(NAVIGATE_VIEW_KEY) ?: NAVIGATE_TO_DETAIL_MEETING

    override fun onExitMeetingRoom() {
        ExitMeetingRoomDialog().show(supportFragmentManager, EXIT_MEETING_ROOM_DIALOG_TAG)
    }

    companion object {
        private const val EXIT_MEETING_ROOM_DIALOG_TAG = "exitMeetingRoomDialog"

        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        private const val NAVIGATE_VIEW_KEY = "navigate_view"
        const val NAVIGATE_TO_DETAIL_MEETING = "detail_meeting"
        const val NAVIGATE_TO_ETA_DASHBOARD = "eta_dashboard"
        private const val NAVIGATE_TO_NOTIFICATION_LOG = "notification_log"

        fun getIntent(
            context: Context,
            meetingId: Long,
            navigateView: String,
        ): Intent {
            return Intent(context, MeetingRoomActivity::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
                putExtra(NAVIGATE_VIEW_KEY, navigateView)
            }
        }
    }
}
