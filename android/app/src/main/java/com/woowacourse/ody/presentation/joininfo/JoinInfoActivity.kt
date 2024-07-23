package com.woowacourse.ody.presentation.joininfo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityJoinInfoBinding
import com.woowacourse.ody.presentation.adapter.InfoViewPagerAdapter
import com.woowacourse.ody.presentation.meetinginfo.BackListener
import com.woowacourse.ody.presentation.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.startingpoint.JoinStartingPointFragment

class JoinInfoActivity : AppCompatActivity(), BackListener {
    private val binding: ActivityJoinInfoBinding by lazy {
        ActivityJoinInfoBinding.inflate(layoutInflater)
    }
    private val viewModel: JoinInfoViewModel by viewModels<JoinInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
    }

    private fun initializeDataBinding() {
        binding.infoListener = this
        initializeJoinInfoViewPager()
    }

    private fun initializeJoinInfoViewPager() {
        val visitorOnBodingInfoAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, getJoinInfoFragments())

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    private fun getJoinInfoFragments(): List<Fragment> =
        listOf(JoinNickNameFragment(), JoinStartingPointFragment())

    override fun onBack() {
        if (binding.vpJoinInfo.currentItem > 0) {
            binding.vpJoinInfo.currentItem -= 1
        } else {
            // 초대 코드 입력 화면 으로 이동
        }
    }
}
