package com.mulberry.ody.presentation.feature.room.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.theme.OdyTheme

class NotificationLogFragment : Fragment() {
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
                    NotificationLogScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}
