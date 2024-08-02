package com.woowacourse.ody.presentation.creation.date

import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingDateBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.creation.MeetingCreationViewModel
import com.woowacourse.ody.presentation.creation.MeetingInfoType
import java.time.LocalDate

class MeetingDateFragment : BindingFragment<FragmentMeetingDateBinding>(R.layout.fragment_meeting_date) {
    private val viewModel: MeetingCreationViewModel by activityViewModels<MeetingCreationViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initializeCalendar()
        initializeObserve()
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
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            viewModel.updateMeetingDate(selectedDate)
        }
    }

    private fun initializeObserve() {
        viewModel.invalidMeetingDateEvent.observe(viewLifecycleOwner) {
            showSnackBar(R.string.meeting_date_date_guide)
            val meetingDate = viewModel.meetingDate.value ?: return@observe
            binding.dpDate.updateDate(meetingDate.year, meetingDate.monthValue - 1, meetingDate.dayOfMonth)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingInfoType.value = MeetingInfoType.DATE
    }
}
