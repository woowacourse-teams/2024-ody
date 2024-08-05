package com.woowacourse.ody.presentation.join

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingJoinBinding
import com.woowacourse.ody.presentation.common.ViewPagerAdapter
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.common.listener.NextListener
import com.woowacourse.ody.presentation.join.complete.JoinCompleteActivity
import com.woowacourse.ody.presentation.join.departure.JoinDepartureFragment
import com.woowacourse.ody.presentation.join.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.room.log.NotificationLogActivity

class MeetingJoinActivity : BindingActivity<ActivityMeetingJoinBinding>(R.layout.activity_meeting_join), NextListener, BackListener {
    private val viewModel: MeetingJoinViewModel by viewModels<MeetingJoinViewModel> {
        MeetingJoinViewModelFactory(getInviteCode(), application.joinRepository, application.matesEtaRepository)
    }
    private val fragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinDepartureFragment())
    }
    private val joinCompletionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }
            viewModel.navigateJoinToRoom()
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

        viewModel.navigateAction.observe(this) {
            when (it) {
                MeetingJoinNavigateAction.JoinNavigateToRoom -> {
                    startActivity(NotificationLogActivity.getIntent(this))
                }
                MeetingJoinNavigateAction.JoinNavigateToJoinComplete -> {
                    joinCompletionLauncher.launch(JoinCompleteActivity.getIntent(this))
                }
            }
        }
    }

    override fun onNext() {
        if (binding.vpJoinInfo.currentItem == fragments.size - 1) {
            viewModel.joinMeeting()
            viewModel.onClickMeetingJoin()
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

    private fun getInviteCode(): String = intent.getStringExtra(INVITE_CODE_KEY) ?: ""

    companion object {
        private const val INVITE_CODE_KEY = "invite_code_key"

        fun getIntent(
            inviteCode: String,
            context: Context,
        ): Intent {
            return Intent(context, MeetingJoinActivity::class.java).apply {
                putExtra(INVITE_CODE_KEY, inviteCode)
            }
        }
    }
}
