package com.woowacourse.ody.presentation.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityIntroBinding
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoActivity
import com.woowacourse.ody.presentation.common.observeEvent

class IntroActivity : AppCompatActivity() {
    private val vm: IntroViewModel by viewModels()
    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted.not()) {
                showSnackBar(R.string.intro_notification_permission_required)
            }
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
        startActivity(InviteCodeActivity.getIntent(this))
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showSnackBar(R.string.intro_notification_permission_guide)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun showSnackBar(
        @StringRes message: Int,
    ) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}
