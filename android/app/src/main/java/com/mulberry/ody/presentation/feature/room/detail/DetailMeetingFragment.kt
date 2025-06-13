package com.mulberry.ody.presentation.feature.room.detail

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.detail.model.InviteCodeCopyInfo
import com.mulberry.ody.presentation.theme.OdyTheme

class DetailMeetingFragment : Fragment() {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                OdyTheme {
                    DetailMeetingScreen(
                        onBack = (requireActivity() as MeetingRoomActivity)::onBack,
                        onCopyInviteCode = ::onCopyInviteCode,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    private fun onCopyInviteCode(info: InviteCodeCopyInfo) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "text/plain"

        val shareMessage = getString(R.string.invite_code_copy, info.meetingName, info.inviteCode)
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage)

        val chooserTitle = getString(R.string.invite_code_copy_title)
        startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
