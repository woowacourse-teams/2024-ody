package com.woowacourse.ody.presentation.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R
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
        requestNotificationPermission()
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
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // 권한 설정 안내 토스트 띄우기
                    showToast(getString(R.string.intro_notification_permission_guide))
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                // 안드로이드 12 이하는 Notification에 관한 권한 필요 없음
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (!isGranted) {
                // 권한 요청 거부한 경우 안내 토스트 띄우기
                showToast(getString(R.string.intro_notification_permission_required))
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}
