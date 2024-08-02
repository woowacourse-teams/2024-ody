package com.woowacourse.ody.presentation.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.woowacourse.ody.presentation.intro.IntroActivity
import com.woowacourse.ody.presentation.join.complete.JoinCompleteActivity
import com.woowacourse.ody.presentation.join.departure.JoinDepartureFragment
import com.woowacourse.ody.presentation.join.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.room.MeetingRoomActivity

class MeetingCreationActivity : BindingActivity<ActivityMeetingCreationBinding>(R.layout.activity_meeting_creation), BackListener {
    private val viewModel: MeetingCreationViewModel by viewModels<MeetingCreationViewModel> {
        MeetingCreationViewModelFactory(
            meetingRepository = application.meetingRepository,
            joinRepository = application.joinRepository,
            inviteCodeRepository = application.inviteCodeRepository,
        )
    }
    private val meetingInfoFragments: List<Fragment> by lazy {
        listOf(
            MeetingNameFragment(),
            MeetingDateFragment(),
            MeetingTimeFragment(),
            MeetingDestinationFragment(),
        )
    }
    private val joinInfoFragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinDepartureFragment())
    }
    private val meetingCompletionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            binding.vpJoinInfo.visibility = View.VISIBLE
            binding.wdJoinInfo.visibility = View.VISIBLE
        }
    private val joinCompletionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            viewModel.navigateToRoom()
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
        initializeVisitorOnBodingInfoViewPager()
    }

    private fun handleBackClick() {
        if (binding.vpMeetingInfo.visibility == View.VISIBLE) {
            handleMeetingInfoBackClick()
        } else {
            handleJoinInfoBackClick()
        }
    }

    private fun handleMeetingInfoBackClick() {
        if (binding.vpMeetingInfo.currentItem > 0) {
            binding.vpMeetingInfo.currentItem -= 1
        } else {
            viewModel.navigateToIntro()
            finish()
        }
    }

    private fun handleJoinInfoBackClick() {
        if (binding.vpJoinInfo.currentItem > 0) {
            binding.vpJoinInfo.currentItem -= 1
        } else {
            binding.vpMeetingInfo.visibility = View.VISIBLE
            binding.wdMeetingInfo.visibility = View.VISIBLE
            binding.vpJoinInfo.visibility = View.GONE
            binding.wdJoinInfo.visibility = View.GONE
        }
    }

    private fun initializeMeetingInfoViewPager() {
        val meetingInfoViewPagerAdapter: ViewPagerAdapter =
            ViewPagerAdapter(this, meetingInfoFragments)

        binding.vpMeetingInfo.adapter = meetingInfoViewPagerAdapter
        binding.wdMeetingInfo.attachTo(binding.vpMeetingInfo)
    }

    private fun initializeVisitorOnBodingInfoViewPager() {
        val visitorOnBodingInfoAdapter: ViewPagerAdapter =
            ViewPagerAdapter(this, joinInfoFragments)

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    private fun initializeObserve() {
        viewModel.nextPageEvent.observe(this) {
            if (binding.vpMeetingInfo.visibility == View.VISIBLE) {
                handleMeetingInfoNextClick()
            } else {
                handleJoinInfoNextClick()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        viewModel.navigateAction.observe(this) {
            when (it) {
                MeetingCreationNavigateAction.NavigateToRoom -> {
                    startActivity(MeetingRoomActivity.getIntent(this))
                }
                MeetingCreationNavigateAction.NavigateToIntro -> {
                    startActivity(IntroActivity.getIntent(this))
                }
                MeetingCreationNavigateAction.NavigateToCreationComplete -> {
                    meetingCompletionLauncher.launch(Intent(MeetingCompletionActivity.getIntent(this)))
                }
                MeetingCreationNavigateAction.NavigateToJoinComplete -> {
                    joinCompletionLauncher.launch(JoinCompleteActivity.getIntent(this))
                }
            }
        }
    }

    private fun handleMeetingInfoNextClick() {
        if (binding.vpMeetingInfo.currentItem == meetingInfoFragments.size - 1) {
            viewModel.onClickCreationMeeting()
            binding.vpMeetingInfo.visibility = View.GONE
            binding.wdMeetingInfo.visibility = View.GONE
            return
        }
        binding.vpMeetingInfo.currentItem += 1
    }

    private fun handleJoinInfoNextClick() {
        if (binding.vpJoinInfo.currentItem == joinInfoFragments.size - 1) {
            viewModel.createMeeting()
            viewModel.onClickJoinMeeting()
            return
        }
        binding.vpJoinInfo.currentItem += 1
    }

    override fun onBack() {
        handleBackClick()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingCreationActivity::class.java)
    }
}
