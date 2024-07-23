package com.woowacourse.ody.presentation.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.databinding.ActivityIntroBinding
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoActivity
import com.woowacourse.ody.util.observeEvent

class IntroActivity : AppCompatActivity() {
    private val vm: IntroViewModel by viewModels()
    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeObserve()
        initializeBinding()
    }

    private fun initializeBinding() {
        binding.listener = vm
        setContentView(binding.root)
    }

    private fun initializeObserve() {
        vm.navigateAction.observeEvent(this) { navigateAction ->
            when (navigateAction) {
                is IntroNavigateAction.NavigateToMeetingInfo ->
                    navigateToMeetingInfoActivity()

                is IntroNavigateAction.NavigateToInviteCode ->
                    navigateToInviteCodeActivity()
            }
        }
    }

    private fun navigateToMeetingInfoActivity() {
        startActivity(MeetingInfoActivity.getIntent(this))
    }

    private fun navigateToInviteCodeActivity() {
        Log.d("Hello", "초대 코드를 입력하는 액티비티로 이동")
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}
