package com.mulberry.ody.presentation.feature.creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import com.mulberry.ody.presentation.feature.join.MeetingJoinActivity
import com.mulberry.ody.presentation.theme.OdyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MeetingCreationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge()
            OdyTheme {
                MeetingCreationScreen(onBack = { finish() })
            }
        }
    }

    private fun navigate(navigateAction: MeetingCreationNavigateAction) {
        when (navigateAction) {
            MeetingCreationNavigateAction.NavigateToMeetings -> {
                finish()
            }

            is MeetingCreationNavigateAction.NavigateToMeetingJoin -> {
                val intent = MeetingJoinActivity.getIntent(navigateAction.inviteCode, this@MeetingCreationActivity)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, MeetingCreationActivity::class.java)
    }
}
