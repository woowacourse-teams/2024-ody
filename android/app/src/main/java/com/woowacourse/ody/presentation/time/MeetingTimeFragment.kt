package com.woowacourse.ody.presentation.time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
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
        initializeView()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun initializeView() {
        binding.npMeetingTimeHour.setRangeValues(MEETING_HOUR_RANGE)
        binding.npMeetingTimeMinute.setRangeValues(MEETING_MINUTE_RANGE)
    }

    private fun NumberPicker.setRangeValues(values: IntRange) {
        wrapSelectorWheel = true
        val displayValues = values.toStrings()
        minValue = 0
        maxValue = displayValues.size - 1
        displayedValues = displayValues.toTypedArray()
    }

    private fun IntRange.toStrings() = map {
        val string = it.toString()
        if (string.length == 1) "0$string" else string
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val MEETING_HOUR_RANGE = 0..23
        private val MEETING_MINUTE_RANGE = 0..59
    }
}
