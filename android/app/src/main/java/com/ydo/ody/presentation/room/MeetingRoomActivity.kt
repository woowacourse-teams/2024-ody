package com.ydo.ody.presentation.room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.ydo.ody.R
import com.ydo.ody.databinding.ActivityMeetingRoomBinding
import com.ydo.ody.presentation.common.binding.BindingActivity
import com.ydo.ody.presentation.common.image.KakaoImageShareHelper
import com.ydo.ody.presentation.common.listener.BackListener
import com.ydo.ody.presentation.room.etadashboard.EtaDashboardFragment
import com.ydo.ody.presentation.room.log.NotificationLogFragment

class MeetingRoomActivity :
    BindingActivity<ActivityMeetingRoomBinding>(R.layout.activity_meeting_room),
    BackListener {
    private val viewModel: MeetingRoomViewModel by viewModels<MeetingRoomViewModel> {
        MeetingRoomViewModelFactory(
            analyticsHelper,
            getMeetingId(),
            application.matesEtaRepository,
            application.notificationLogRepository,
            application.meetingRepository,
            application.imageStorage,
            KakaoImageShareHelper(this),
        )
    }
    private val fragments: Map<String, Fragment> by lazy {
        mapOf(
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

        viewModel.navigateToEtaDashboardEvent.observe(this) {
            addFragment(EtaDashboardFragment())
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

    private fun getNavigateView(): String = intent.getStringExtra(NAVIGATE_VIEW_KEY) ?: NAVIGATE_TO_NOTIFICATION_LOG

    companion object {
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        private const val NAVIGATE_VIEW_KEY = "navigate_view"
        const val NAVIGATE_TO_ETA_DASHBOARD = "eta_dashboard"
        const val NAVIGATE_TO_NOTIFICATION_LOG = "notification_log"

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
