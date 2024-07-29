package com.woowacourse.ody.presentation.join.complete

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.R
import com.woowacourse.ody.data.remote.core.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.presentation.room.MeetingRoomActivity

class JoinCompleteActivity : AppCompatActivity() {
    private val application: OdyApplication by lazy {
        applicationContext as OdyApplication
    }
    private val viewModel: JoinCompleteViewModel by viewModels<JoinCompleteViewModel> {
        application.joinCompleteViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_complete)

        intent.getStringArrayListExtra(MEETING_REQUEST_KEY)?.let {
            postMeeting(it)
        }
        intent.getStringArrayListExtra(JOIN_REQUEST_KEY)?.let {
            postMates(it)
        }

        viewModel.navigateAction.observe(this) {
            startActivity(MeetingRoomActivity.getIntent(this@JoinCompleteActivity, viewModel.meetingResponse.value))
        }
    }

    private fun postMeeting(meetingInfo: ArrayList<String>?) {
        meetingInfo ?: return
        viewModel.postMeeting(
            MeetingRequest(
                meetingInfo[0],
                meetingInfo[1],
                meetingInfo[2],
                meetingInfo[3],
                meetingInfo[4],
                meetingInfo[5],
                meetingInfo[6],
                meetingInfo[7],
                meetingInfo[8],
                meetingInfo[9],
            ),
        )
    }

    private fun postMates(joinInfo: ArrayList<String>?) {
        joinInfo ?: return

        viewModel.postMates(
            JoinRequest(
                joinInfo[0],
                joinInfo[1],
                joinInfo[2],
                joinInfo[3],
                joinInfo[4],
            ),
        )
    }

    companion object {
        const val JOIN_REQUEST_KEY = "join_complete_request_key"
        const val MEETING_REQUEST_KEY = "meeting_complete_request_key"

        fun getMeetingInfoIntent(
            context: Context,
            meetingInfo: ArrayList<String>,
        ): Intent {
            return Intent(context, JoinCompleteActivity::class.java).apply {
                putStringArrayListExtra(MEETING_REQUEST_KEY, meetingInfo)
            }
        }

        fun getJoinInfoIntent(
            context: Context,
            joinInfo: ArrayList<String>,
        ): Intent {
            return Intent(context, JoinCompleteActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putStringArrayListExtra(JOIN_REQUEST_KEY, joinInfo)
            }
        }
    }
}
