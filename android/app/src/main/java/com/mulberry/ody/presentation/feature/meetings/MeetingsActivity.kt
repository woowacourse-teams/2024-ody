package com.mulberry.ody.presentation.feature.meetings

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.mulberry.ody.presentation.common.LoadingDialog
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.feature.creation.MeetingCreationActivity
import com.mulberry.ody.presentation.feature.invitecode.InviteCodeActivity
import com.mulberry.ody.presentation.feature.login.LoginActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity.Companion.NAVIGATE_TO_DETAIL_MEETING
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity.Companion.NAVIGATE_TO_ETA_DASHBOARD
import com.mulberry.ody.presentation.feature.setting.SettingActivity
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeetingsActivity : ComponentActivity() {
    private val viewModel: MeetingsViewModel by viewModels<MeetingsViewModel>()
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                MeetingsScreen(navigate = ::navigate)
            }
        }

        initializeObserve()
    }

    private fun navigate(action: MeetingsNavigateAction) {
        when (action) {
            is MeetingsNavigateAction.NavigateToEtaDashboard -> {
                val intent = MeetingRoomActivity.getIntent(this, action.meetingId, NAVIGATE_TO_ETA_DASHBOARD)
                startActivity(intent)

            }

            is MeetingsNavigateAction.NavigateToNotificationLog -> {
                val intent = MeetingRoomActivity.getIntent(this, action.meetingId, NAVIGATE_TO_DETAIL_MEETING)
                startActivity(intent)
            }

            is MeetingsNavigateAction.NavigateToLogin -> {
                val intent = LoginActivity.getIntent(this)
                startActivity(intent)
            }

            MeetingsNavigateAction.NavigateToCreateMeeting -> {
                val intent = InviteCodeActivity.getIntent(this)
                startActivity(intent)
            }

            MeetingsNavigateAction.NavigateToJoinMeeting -> {
                val intent = MeetingCreationActivity.getIntent(this)
                startActivity(intent)
            }

            MeetingsNavigateAction.NavigateToSetting -> {

                val intent = SettingActivity.getIntent(this)
                startActivity(intent)
            }
        }
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
    }

    private fun showLoadingDialog() {
        dialog?.dismiss()
        dialog = LoadingDialog(this)
        dialog?.show()
    }

    private fun hideLoadingDialog() {
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog = null
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingsActivity::class.java)
    }
}
