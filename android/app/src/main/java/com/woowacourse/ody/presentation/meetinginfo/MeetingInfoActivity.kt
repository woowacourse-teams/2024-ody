package com.woowacourse.ody.presentation.meetinginfo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityMeetingInfoBinding
import com.woowacourse.ody.presentation.adapter.InfoViewPagerAdapter
import com.woowacourse.ody.presentation.date.MeetingDateFragment
import com.woowacourse.ody.presentation.destination.MeetingDestinationFragment
import com.woowacourse.ody.presentation.intro.IntroActivity
import com.woowacourse.ody.presentation.name.MeetingNameFragment
import com.woowacourse.ody.presentation.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.startingpoint.JoinStartingPointFragment
import com.woowacourse.ody.presentation.time.MeetingTimeFragment
import com.woowacourse.ody.util.NextListener

class MeetingInfoActivity : AppCompatActivity(), NextListener, BackListener {
    private val binding: ActivityMeetingInfoBinding by lazy {
        ActivityMeetingInfoBinding.inflate(layoutInflater)
    }
    private val viewModel: MeetingInfoViewModel by viewModels<MeetingInfoViewModel>()
    private val meetingInfoFragments: List<Fragment> by lazy {
        listOf(MeetingNameFragment(), MeetingDateFragment(), MeetingTimeFragment(), MeetingDestinationFragment())
    }
    private val joinInfoFragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinStartingPointFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
    }

    private fun initializeDataBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.nextListener = this
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
            startActivity(IntroActivity.getIntent(this))
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
        val meetingInfoViewPagerAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, meetingInfoFragments)

        binding.vpMeetingInfo.adapter = meetingInfoViewPagerAdapter
        binding.wdMeetingInfo.attachTo(binding.vpMeetingInfo)
    }

    private fun initializeVisitorOnBodingInfoViewPager() {
        val visitorOnBodingInfoAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, joinInfoFragments)

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    override fun onNext() {
        TODO("Not yet implemented")
    }

    override fun onBack() {
        handleBackClick()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingInfoActivity::class.java)
    }
}
