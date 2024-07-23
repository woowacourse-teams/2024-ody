package com.woowacourse.ody.presentation.time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.databinding.FragmentMeetingTimeBinding
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoViewModel

class MeetingTimeFragment : Fragment() {
    private var _binding: FragmentMeetingTimeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingInfoViewModel by activityViewModels<MeetingInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        binding.npMeetingTimeHour.wrapSelectorWheel = true
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
