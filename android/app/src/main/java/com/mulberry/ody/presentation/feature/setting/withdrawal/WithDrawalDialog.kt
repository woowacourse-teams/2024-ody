package com.mulberry.ody.presentation.feature.setting.withdrawal

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.databinding.DialogWithdrawalBinding
import com.mulberry.ody.presentation.common.listener.setOnSingleClickListener
import com.mulberry.ody.presentation.feature.setting.SettingViewModel

class WithDrawalDialog : DialogFragment() {
    private var _binding: DialogWithdrawalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogWithdrawalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.tvWithdrawal.setOnSingleClickListener {
            viewModel.withdrawAccount()
            dismiss()
        }
        binding.tvWithdrawalCancel.setOnSingleClickListener { dismiss() }
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
