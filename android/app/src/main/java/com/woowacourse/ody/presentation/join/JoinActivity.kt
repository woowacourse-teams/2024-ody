package com.woowacourse.ody.presentation.join

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.databinding.ActivityJoinBinding
import com.woowacourse.ody.presentation.common.ViewPagerAdapter
import com.woowacourse.ody.presentation.join.complete.JoinCompleteActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.meeting.MeetingInfoViewModel
import com.woowacourse.ody.presentation.join.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.join.departure.JoinDepartureFragment
import com.woowacourse.ody.presentation.common.listener.NextListener

class JoinActivity : AppCompatActivity(), NextListener, BackListener {
    private val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }
    private val viewModel: MeetingInfoViewModel by viewModels<MeetingInfoViewModel>()
    private val fragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinDepartureFragment())
    }
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initializeDataBinding()
        initializeObserve()
    }

    private fun initializeDataBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.nextListener = this
        binding.backListener = this
        initializeJoinInfoViewPager()
    }

    private fun initializeJoinInfoViewPager() {
        val visitorOnBodingInfoAdapter: ViewPagerAdapter =
            ViewPagerAdapter(this, fragments)

        binding.vpJoinInfo.adapter = visitorOnBodingInfoAdapter
        binding.wdJoinInfo.attachTo(binding.vpJoinInfo)
    }

    private fun initializeObserve() {
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNext() {
        if (binding.vpJoinInfo.currentItem == fragments.size - 1) {
            val startingPointGeoLocation = viewModel.startingPointGeoLocation.value ?: return

            val joinInfo =
                arrayListOf(
                    getInviteCode() ?: return,
                    viewModel.nickname.value.toString(),
                    startingPointGeoLocation.address,
                    startingPointGeoLocation.latitude.slice(0..8),
                    startingPointGeoLocation.longitude.slice(0..8),
                )
            startActivity(JoinCompleteActivity.getJoinInfoIntent(this, joinInfo))
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

    private fun getInviteCode(): String? = intent.getStringExtra(INVITE_CODE_KEY)

    companion object {
        private const val INVITE_CODE_KEY = "invite_code_key"

        fun getIntent(
            inviteCode: String,
            context: Context,
        ): Intent {
            return Intent(context, JoinActivity::class.java).apply {
                putExtra(INVITE_CODE_KEY, inviteCode)
            }
        }
    }
}
