package com.woowacourse.ody.presentation.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingCreationBinding
import com.woowacourse.ody.presentation.common.ViewPagerAdapter
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.creation.complete.MeetingCompletionActivity
import com.woowacourse.ody.presentation.creation.date.MeetingDateFragment
import com.woowacourse.ody.presentation.creation.destination.MeetingDestinationFragment
import com.woowacourse.ody.presentation.creation.name.MeetingNameFragment
import com.woowacourse.ody.presentation.creation.time.MeetingTimeFragment
import com.woowacourse.ody.presentation.join.MeetingJoinActivity

class MeetingCreationActivity :
    BindingActivity<ActivityMeetingCreationBinding>(R.layout.activity_meeting_creation),
    BackListener {
    private val viewModel: MeetingCreationViewModel by viewModels<MeetingCreationViewModel> {
        MeetingCreationViewModelFactory(
            firebaseAnalytics = application.firebaseAnalytics,
            meetingRepository = application.meetingRepository,
        )
    }
    private val fragments: List<Fragment> by lazy {
        listOf(
            MeetingNameFragment(),
            MeetingDateFragment(),
            MeetingTimeFragment(),
            MeetingDestinationFragment(),
        )
    }
    private val meetingCompletionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            val inviteCode = viewModel.inviteCode.value ?: return@registerForActivityResult
            startActivity(MeetingJoinActivity.getIntent(inviteCode, this))
            finish()
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
        binding.backListener = this
        initializeMeetingInfoViewPager()
    }

    private fun initializeMeetingInfoViewPager() {
        val meetingInfoViewPagerAdapter: ViewPagerAdapter =
            ViewPagerAdapter(this, fragments)

        binding.vpMeetingInfo.adapter = meetingInfoViewPagerAdapter
        binding.wdMeetingInfo.attachTo(binding.vpMeetingInfo)
    }

    private fun initializeObserve() {
        viewModel.inviteCode.observe(this) {
            viewModel.onClickCreationMeeting()
        }
        viewModel.nextPageEvent.observe(this) {
            handleMeetingInfoNextClick()
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel.navigateAction.observe(this) {
            when (it) {
                MeetingCreationNavigateAction.NavigateToMeetings -> {
                    finish()
                }

                MeetingCreationNavigateAction.NavigateToCreationComplete -> {
                    meetingCompletionLauncher.launch(Intent(MeetingCompletionActivity.getIntent(this)))
                }
            }
        }
    }

    private fun handleMeetingInfoNextClick() {
        if (binding.vpMeetingInfo.currentItem == fragments.size - 1) {
            viewModel.createMeeting()
            return
        }
        binding.vpMeetingInfo.currentItem += 1
    }

    override fun onBack() {
        if (binding.vpMeetingInfo.currentItem > 0) {
            binding.vpMeetingInfo.currentItem -= 1
        } else {
            viewModel.navigateToIntro()
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingCreationActivity::class.java)
    }
}
