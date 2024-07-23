package com.woowacourse.ody.presentation.meetinginfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityMeetingInfoBinding
import com.woowacourse.ody.presentation.IntroActivity
import com.woowacourse.ody.presentation.date.MeetingDateFragment
import com.woowacourse.ody.presentation.destination.MeetingDestinationFragment
import com.woowacourse.ody.presentation.meetinginfo.adapter.InfoViewPagerAdapter
import com.woowacourse.ody.presentation.name.MeetingNameFragment
import com.woowacourse.ody.presentation.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.startingpoint.JoinStartingPointFragment
import com.woowacourse.ody.presentation.time.MeetingTimeFragment

class MeetingInfoActivity : AppCompatActivity(), MeetingInfoListener {
    private val binding: ActivityMeetingInfoBinding by lazy {
        ActivityMeetingInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDataBinding()
        initMeetingInfoViewPager()
        initVisitorOnBodingInfoViewPager()
    }

    private fun initDataBinding() {
        binding.meetingInfoListener = this
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

    private fun initMeetingInfoViewPager() {
        val aAdapter: InfoViewPagerAdapter = InfoViewPagerAdapter(this, getMeetingInfoFragments())

        binding.vpMeetingInfo.adapter = aAdapter
        binding.wdMeetingInfo.attachTo(binding.vpMeetingInfo)
    }

    private fun initVisitorOnBodingInfoViewPager() {
        val bAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, getVisitorOnBodingInfoFragments())

        binding.vpJoinInfo.adapter = bAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    private fun getMeetingInfoFragments(): List<Fragment> {
        return listOf(
            MeetingNameFragment(),
            MeetingDateFragment(),
            MeetingTimeFragment(),
            MeetingDestinationFragment(),
        )
    }

    private fun getVisitorOnBodingInfoFragments(): List<Fragment> {
        return listOf(JoinNickNameFragment(), JoinStartingPointFragment())
    }

    override fun onBack() {
        handleBackClick()
    }
}
