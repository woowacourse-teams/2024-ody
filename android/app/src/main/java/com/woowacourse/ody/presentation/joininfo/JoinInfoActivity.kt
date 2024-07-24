package com.woowacourse.ody.presentation.joininfo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityJoinInfoBinding
import com.woowacourse.ody.presentation.adapter.InfoViewPagerAdapter
import com.woowacourse.ody.presentation.completion.JoinCompleteActivity
import com.woowacourse.ody.presentation.meetinginfo.BackListener
import com.woowacourse.ody.presentation.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.startingpoint.JoinStartingPointFragment
import com.woowacourse.ody.util.NextListener

class JoinInfoActivity : AppCompatActivity(), NextListener, BackListener {
    private val binding: ActivityJoinInfoBinding by lazy {
        ActivityJoinInfoBinding.inflate(layoutInflater)
    }
    private val viewModel: JoinInfoViewModel by viewModels<JoinInfoViewModel>()
    private val fragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinStartingPointFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
        initializeJoinInfoViewPager()
    }

    private fun initializeDataBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.nextListener = this
        binding.backListener = this
    }

    private fun initializeJoinInfoViewPager() {
        val visitorOnBodingInfoAdapter: InfoViewPagerAdapter =
            InfoViewPagerAdapter(this, fragments)

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    override fun onNext() {
        if (binding.vpJoinInfo.currentItem == fragments.size - 1) {
            startActivity(JoinCompleteActivity.getIntent(this))
            finish()
            return
        }
        binding.vpJoinInfo.currentItem += 1
    }

    override fun onBack() {
        if (binding.vpJoinInfo.currentItem > 0) {
            binding.vpJoinInfo.currentItem -= 1
            return
        }
        finish()
    }
}
