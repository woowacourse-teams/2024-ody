package com.woowacourse.ody.presentation.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingDateBinding
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoViewModel
import java.util.Calendar

class MeetingDateFragment : Fragment() {
    private var _binding: FragmentMeetingDateBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingInfoViewModel by activityViewModels<MeetingInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initializeCalendar()
        viewModel.onNextInfo()
    }

    private fun initializeCalendar() {
        val today = Calendar.getInstance().timeInMillis
        binding.cvDate.minDate = today
        binding.cvDate.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (selectedDate.timeInMillis < Calendar.getInstance().timeInMillis) {
                showSnackBar(getString(R.string.meeting_date_date_guide))
                binding.cvDate.date = today
            } else {
                viewModel.meetingYear.value = year
                viewModel.meetingMonth.value = month + 1
                viewModel.meetingDay.value = dayOfMonth
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackBar(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
}
