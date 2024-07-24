package com.woowacourse.ody.presentation.time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingTimeBinding
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoViewModel
import com.woowacourse.ody.util.observeEvent

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
        viewModel.isValidMeetingTime.observeEvent(viewLifecycleOwner) { isValid ->
            if (isValid) {
                // 다음 화면으로 이동
                return@observeEvent
            }
            showSnackBar(R.string.invalid_meeting_time)
        }
    }

    private fun showSnackBar(
        @StringRes messageId: Int,
    ) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()
    }

    private fun initializeView() {
        binding.npMeetingTimeHour.wrapSelectorWheel = true
        binding.npMeetingTimeMinute.wrapSelectorWheel = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
