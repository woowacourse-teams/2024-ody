package com.mulberry.ody.presentation.room.log

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.databinding.DialogExitRoomBinding
import com.mulberry.ody.presentation.launchWhenStarted
import com.mulberry.ody.presentation.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.room.log.listener.ExitRoomListener
import kotlinx.coroutines.launch

class ExitRoomDialog : DialogFragment(), ExitRoomListener {
    private var _binding: DialogExitRoomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingRoomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogExitRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        initializeBinding()
        initializeObserve()
    }

    private fun initializeView() {
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initializeBinding() {
        binding.lifecycleOwner = this
        binding.exitRoomListener = this
    }

    private fun initializeObserve() {
        launchWhenStarted {
            launch {
                viewModel.meeting.collect {
                    binding.meetingName = it.name
                }
            }
            launch {
                viewModel.exitMeetingEvent.collect {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancel() {
        dismiss()
    }

    override fun onExit() {
        viewModel.exitRoom()
    }
}
