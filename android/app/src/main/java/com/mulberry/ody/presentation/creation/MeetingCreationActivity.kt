package com.mulberry.ody.presentation.creation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.ActivityMeetingCreationBinding
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.address.AddressSearchFragment
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.common.ViewPagerAdapter
import com.mulberry.ody.presentation.common.binding.BindingActivity
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.creation.complete.MeetingCompletionActivity
import com.mulberry.ody.presentation.creation.date.MeetingDateFragment
import com.mulberry.ody.presentation.creation.destination.MeetingDestinationFragment
import com.mulberry.ody.presentation.creation.name.MeetingNameFragment
import com.mulberry.ody.presentation.creation.time.MeetingTimeFragment
import com.mulberry.ody.presentation.join.MeetingJoinActivity
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
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        viewModel.inviteCode.observe(this) {
            viewModel.onClickCreationMeeting()
        }
        viewModel.nextPageEvent.observe(this) {
            handleMeetingInfoNextClick()
        }
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
