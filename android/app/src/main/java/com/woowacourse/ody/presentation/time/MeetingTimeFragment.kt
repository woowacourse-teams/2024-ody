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
        initializeObserve()
        initializeView()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun initializeObserve() {
        viewModel.meetingHour.observe(viewLifecycleOwner) {
            binding.npMeetingTimeHour.setSelectedValue(it)
        }
        viewModel.meetingMinute.observe(viewLifecycleOwner) {
            binding.npMeetingTimeMinute.setSelectedValue(it)
        }
    }

    private fun NumberPicker.setSelectedValue(value: Int) {
        val index = displayedValues.indexOf(value.toTwoLengthString())
        setValue(index)
    }

    private fun initializeView() {
        binding.npMeetingTimeHour.setRangeValues(MEETING_HOUR_RANGE.toStrings())
        binding.npMeetingTimeMinute.setRangeValues(MEETING_MINUTE_RANGE.toStrings())
    }

    private fun IntRange.toStrings() = map { it.toTwoLengthString() }

    private fun Int.toTwoLengthString(): String {
        val string = toString()
        return if (string.length == 1) "0$string" else string
    }

    private fun NumberPicker.setRangeValues(values: List<String>) {
        wrapSelectorWheel = true
        minValue = 0
        maxValue = values.size - 1
        displayedValues = values.toTypedArray()
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
