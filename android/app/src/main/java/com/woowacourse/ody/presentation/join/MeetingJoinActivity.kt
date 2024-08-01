package com.woowacourse.ody.presentation.join

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.databinding.ActivityMeetingJoinBinding
import com.woowacourse.ody.presentation.common.ViewPagerAdapter
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.common.listener.NextListener
import com.woowacourse.ody.presentation.creation.MeetingCreationViewModel
import com.woowacourse.ody.presentation.creation.MeetingCreationViewModelFactory
import com.woowacourse.ody.presentation.join.complete.JoinCompleteActivity
import com.woowacourse.ody.presentation.join.departure.JoinDepartureFragment
import com.woowacourse.ody.presentation.join.nickname.JoinNickNameFragment
import com.woowacourse.ody.presentation.room.MeetingRoomActivity

class MeetingJoinActivity : AppCompatActivity(), NextListener, BackListener {
    private val application: OdyApplication by lazy {
        applicationContext as OdyApplication
    }
    private val binding: ActivityMeetingJoinBinding by lazy {
        ActivityMeetingJoinBinding.inflate(layoutInflater)
    }
    private val viewModel: MeetingCreationViewModel by viewModels<MeetingCreationViewModel> {
        MeetingCreationViewModelFactory(
            meetingRepository = application.meetingRepository,
            joinRepository = application.joinRepository,
            inviteCodeRepository = application.inviteCodeRepository,
        )
    }
    private val fragments: List<Fragment> by lazy {
        listOf(JoinNickNameFragment(), JoinDepartureFragment())
    }
    private val joinCompletionLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                return@registerForActivityResult
            }

            startActivity(MeetingRoomActivity.getIntent(this, viewModel.makeMeetingResponse.value))
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
            val inviteCode: String = getInviteCode() ?: return
            viewModel.joinMeeting(inviteCode)
            joinCompletionLauncher.launch(JoinCompleteActivity.getIntent(this))
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
            return Intent(context, MeetingJoinActivity::class.java).apply {
                putExtra(INVITE_CODE_KEY, inviteCode)
            }
        }
    }
}
