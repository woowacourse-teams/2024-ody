package com.woowacourse.ody.presentation.meetinginfo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InfoViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val meetingInfoFragments: List<Fragment>,
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = meetingInfoFragments.size

    override fun createFragment(position: Int): Fragment = meetingInfoFragments[position]
}
