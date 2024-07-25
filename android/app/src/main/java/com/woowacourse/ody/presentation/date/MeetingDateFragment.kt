package com.woowacourse.ody.presentation.date

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingDateBinding
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoType
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoViewModel
import java.time.LocalDate

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
    }

    private fun initializeCalendar() {
        val dpHeaderId =
            binding.dpDate.getChildAt(0).resources.getIdentifier(
                "date_picker_header",
                "id",
                "android",
            )
        binding.dpDate.findViewById<View>(dpHeaderId).visibility = View.GONE

        binding.dpDate.minDate = System.currentTimeMillis()

        binding.dpDate.setOnDateChangedListener { _, year, month, dayOfMonth ->
            val now = LocalDate.now()
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

            if (now.isAfter(selectedDate)) {
                showSnackBar(R.string.meeting_date_date_guide)
                return@setOnDateChangedListener
            }

            viewModel.meetingYear.value = year
            viewModel.meetingMonth.value = month + 1
            viewModel.meetingDay.value = dayOfMonth
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingInfoType.value = MeetingInfoType.DATE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackBar(
        @StringRes message: Int,
    ) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        .apply { setAnchorView(activity?.findViewById(R.id.btn_next)) }
        .show()
}
