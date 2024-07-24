package com.woowacourse.ody.presentation.nickname

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.databinding.FragmentJoinNicknameBinding
import com.woowacourse.ody.presentation.joininfo.JoinInfoViewModel

class JoinNickNameFragment : Fragment() {
    private var _binding: FragmentJoinNicknameBinding? = null
    private val binding get() = _binding!!

    private val viewModel: JoinInfoViewModel by activityViewModels<JoinInfoViewModel>()

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
        viewModel.onNextInfo()
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
