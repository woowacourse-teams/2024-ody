package com.woowacourse.ody.presentation.join.nickname

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.databinding.FragmentJoinNicknameBinding
import com.woowacourse.ody.presentation.meeting.MeetingInfoType
import com.woowacourse.ody.presentation.meeting.MeetingInfoViewModel

class JoinNickNameFragment : Fragment() {
    private var _binding: FragmentJoinNicknameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingInfoViewModel by activityViewModels<MeetingInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentJoinNicknameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingInfoType.value = MeetingInfoType.NICKNAME
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
