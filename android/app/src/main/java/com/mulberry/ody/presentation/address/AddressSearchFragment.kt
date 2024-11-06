package com.mulberry.ody.presentation.address

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentAddressSearchBinding
import com.mulberry.ody.presentation.address.adapter.AddressesAdapter
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.listener.BackListener
import com.mulberry.ody.presentation.common.toPixel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressSearchFragment :
    BindingFragment<FragmentAddressSearchBinding>(R.layout.fragment_address_search), BackListener {
    private val viewModel by viewModels<AddressSearchViewModel>()
    private val adapter by lazy { AddressesAdapter(viewModel) }

    private val onBackPressedCallback: OnBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeAddressAdapter()
        initializeBinding()
        initializeObserve()
        binding.etAddressSearchKeyword.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.searchAddress()
            }
            false
        }
    }

    private fun initializeAddressAdapter() {
        val horizontalMarginPixel = ADDRESS_ITEM_HORIZONTAL_MARGIN_DP.toPixel(requireContext())
        val dividerItemDecoration =
            MaterialDividerItemDecoration(requireContext(), LinearLayout.VERTICAL).apply {
                isLastItemDecorated = false
                dividerColor = ContextCompat.getColor(requireContext(), R.color.gray_350)
                dividerInsetStart = horizontalMarginPixel
                dividerInsetEnd = horizontalMarginPixel
            }
        binding.rvAddress.addItemDecoration(dividerItemDecoration)
        binding.rvAddress.adapter = adapter
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.backListener = this
    }

    private fun initializeObserve() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback,
        )
        collectWhenStarted(viewModel.isLoading) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@collectWhenStarted
            }
            hideLoadingDialog()
        }
        collectWhenStarted(viewModel.networkErrorEvent) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }
        collectWhenStarted(viewModel.addressUiModels) {
            adapter.submitList(it)
        }
        collectWhenStarted(viewModel.addressSelectEvent) {
            (parentFragment as? AddressSearchListener)?.onReceive(it)
            (activity as? AddressSearchListener)?.onReceive(it)
            onBack()
        }
        collectWhenStarted(viewModel.addressSearchKeyword) {
            if (it.isEmpty()) viewModel.clearAddresses()
        }
    }

    override fun onBack() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val ADDRESS_ITEM_HORIZONTAL_MARGIN_DP = 26
    }
}
