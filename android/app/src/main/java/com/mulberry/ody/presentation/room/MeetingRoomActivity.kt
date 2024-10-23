package com.mulberry.ody.presentation.room

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingRoomBinding
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.room.etadashboard.EtaDashboardFragment
import com.mulberry.ody.presentation.room.log.NotificationLogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MeetingRoomActivity :
    BindingActivity<ActivityMeetingRoomBinding>(R.layout.activity_meeting_room),
    BackListener {
    @Inject
    lateinit var viewModelFactory: MeetingRoomViewModel.MeetingViewModelFactory

    private val viewModel: MeetingRoomViewModel by viewModels<MeetingRoomViewModel> {
        MeetingRoomViewModel.provideFactory(viewModelFactory, this, meetingId = getMeetingId())
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
        lifecycleScope.launch {
            viewModel.navigateToEtaDashboardEvent.collect {
                addFragment(EtaDashboardFragment())
            }
        }
        lifecycleScope.launch {
            viewModel.networkErrorEvent.collect {
                showRetrySnackBar { viewModel.retryLastAction() }
            }
        }
        lifecycleScope.launch {
            viewModel.errorEvent.collect {
                showSnackBar(R.string.error_guide)
            }
        }
        lifecycleScope.launch {
            viewModel.expiredNudgeTimeLimit.collect {
                showSnackBar(R.string.nudge_time_limit_expired)
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    showLoadingDialog()
                    return@collect
                }
                hideLoadingDialog()
            }
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
