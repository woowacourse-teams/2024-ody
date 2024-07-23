package com.woowacourse.ody.presentation.joininfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityJoinInfoBinding
import com.woowacourse.ody.presentation.meetinginfo.InfoListener
import com.woowacourse.ody.presentation.meetinginfo.adapter.InfoViewPagerAdapter
import com.woowacourse.ody.presentation.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.startingpoint.JoinStartingPointFragment

class JoinInfoActivity : AppCompatActivity(), InfoListener {
    private val binding: ActivityJoinInfoBinding by lazy {
        ActivityJoinInfoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
        initVisitorInfoViewPager()
    }

    private fun initializeDataBinding() {
        binding.infoListener = this
    }

    private fun initVisitorInfoViewPager() {
        val visitorOnBodingInfoAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, getVisitorOnBodingInfoFragments())

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    private fun getVisitorOnBodingInfoFragments(): List<Fragment> {
        return listOf(JoinNickNameFragment(), JoinStartingPointFragment())
    }

    override fun onBack() {
        if (binding.vpJoinInfo.currentItem > 0) {
            binding.vpJoinInfo.currentItem -= 1
        } else {
            // 초대 코드 입력 화면 으로 이동
        }
    }
}
