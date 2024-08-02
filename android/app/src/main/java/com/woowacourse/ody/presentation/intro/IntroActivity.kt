package com.woowacourse.ody.presentation.intro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityIntroBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.creation.MeetingCreationActivity
import com.woowacourse.ody.presentation.invitecode.InviteCodeActivity

class IntroActivity : BindingActivity<ActivityIntroBinding>(R.layout.activity_intro) {
    private val viewModel: IntroViewModel by viewModels()
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

    override fun initializeBinding() {
        binding.listener = viewModel
    }

    private fun initializeObserve() {
        viewModel.navigateAction.observe(this) { navigateAction ->
            when (navigateAction) {
                is IntroNavigateAction.NavigateToMeetingInfo ->
                    navigateToMeetingInfoActivity()

                is IntroNavigateAction.NavigateToInviteCode ->
                    navigateToInviteCodeActivity()
            }
        }
    }

    private fun navigateToMeetingInfoActivity() {
        startActivity(MeetingCreationActivity.getIntent(this))
    }

    private fun navigateToInviteCodeActivity() {
        startActivity(InviteCodeActivity.getIntent(this))
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showSnackBar(R.string.intro_notification_permission_guide)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, IntroActivity::class.java)
    }
}
