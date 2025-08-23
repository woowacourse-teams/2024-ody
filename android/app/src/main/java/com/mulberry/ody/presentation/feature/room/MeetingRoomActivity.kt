package com.mulberry.ody.presentation.feature.room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.feature.room.detail.DetailMeetingFragment
import com.mulberry.ody.presentation.feature.room.etadashboard.EtaDashboardFragment
import com.mulberry.ody.presentation.feature.room.log.NotificationLogFragment
import com.mulberry.ody.presentation.feature.room.model.MeetingRoomNavigateAction
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MeetingRoomActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_meeting_room)
        initializeObserve()
        if (savedInstanceState == null) {
            initializeFragment()
        }
    }

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        collectWhenStarted(viewModel.navigationEvent) {
            val fragment =
                when (it) {
                    MeetingRoomNavigateAction.NavigateToEtaDashboard -> fragments[NAVIGATE_TO_ETA_DASHBOARD]
                    MeetingRoomNavigateAction.NavigateToNotificationLog -> fragments[NAVIGATE_TO_NOTIFICATION_LOG]
                } ?: return@collectWhenStarted
            addFragment(fragment)
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

    fun onBack() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun getMeetingId(): Long = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)

    private fun getNavigateView(): String = intent.getStringExtra(NAVIGATE_VIEW_KEY) ?: NAVIGATE_TO_DETAIL_MEETING

    companion object {
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
