package com.woowacourse.ody.presentation.join.nickname

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentJoinNicknameBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.join.MeetingJoinViewModel

class JoinNickNameFragment : BindingFragment<FragmentJoinNicknameBinding>(R.layout.fragment_join_nickname) {
    private val viewModel: MeetingJoinViewModel by activityViewModels<MeetingJoinViewModel>()

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
}
