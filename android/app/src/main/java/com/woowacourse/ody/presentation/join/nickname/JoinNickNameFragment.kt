package com.woowacourse.ody.presentation.join.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentJoinNicknameBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.creation.MeetingCreationViewModel
import com.woowacourse.ody.presentation.creation.MeetingInfoType

class JoinNickNameFragment : BindingFragment<FragmentJoinNicknameBinding>(R.layout.fragment_join_nickname) {
    private val viewModel: MeetingCreationViewModel by activityViewModels<MeetingCreationViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingInfoType.value = MeetingInfoType.NICKNAME
    }
}
