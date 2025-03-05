package com.mulberry.ody.presentation.feature.creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingCreationBinding
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.common.ViewPagerAdapter
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.feature.address.AddressSearchFragment
import com.mulberry.ody.presentation.feature.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.feature.creation.date.MeetingDateFragment
import com.mulberry.ody.presentation.feature.creation.destination.MeetingDestinationFragment
import com.mulberry.ody.presentation.feature.creation.name.MeetingNameFragment
import com.mulberry.ody.presentation.feature.creation.time.MeetingTimeFragment
import com.mulberry.ody.presentation.feature.join.MeetingJoinActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeetingCreationActivity :
    BindingActivity<ActivityMeetingCreationBinding>(R.layout.activity_meeting_creation),
    BackListener,
    AddressSearchListener {
    private val viewModel: MeetingCreationViewModel by viewModels<MeetingCreationViewModel>()
    private val fragments: List<Fragment> by lazy {
        listOf(
            MeetingNameFragment(),
            MeetingDestinationFragment(),
            MeetingDateFragment(),
            MeetingTimeFragment(),
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
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        collectWhenStarted(viewModel.inviteCode) {
            viewModel.onClickCreationMeeting()
        }
        collectWhenStarted(viewModel.nextPageEvent) {
            handleMeetingInfoNextClick()
        }
        collectWhenStarted(viewModel.navigateAction) {
            when (it) {
                MeetingCreationNavigateAction.NavigateToMeetings -> {
                    finish()
                }

                is MeetingCreationNavigateAction.NavigateToMeetingJoin -> {
                    startActivity(
                        MeetingJoinActivity.getIntent(
                            it.inviteCode,
                            this@MeetingCreationActivity,
                        ),
                    )
                    finish()
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

    override fun onSearch() {
        supportFragmentManager.commit {
            add(R.id.fcv_creation, AddressSearchFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onReceive(address: Address) {
        viewModel.destinationAddress.value = address
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingCreationActivity::class.java)
    }
}
