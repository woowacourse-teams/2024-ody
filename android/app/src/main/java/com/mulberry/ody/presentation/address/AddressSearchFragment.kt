package com.mulberry.ody.presentation.address

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentAddressSearchBinding
import com.mulberry.ody.presentation.address.adapter.AddressesAdapter
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.listener.BackListener
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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoadingDialog()
                return@observe
            }
            hideLoadingDialog()
        }
        viewModel.networkErrorEvent.observe(this) {
            showRetrySnackBar { viewModel.retryLastAction() }
        }

        viewModel.addressUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.addressSelectEvent.observe(viewLifecycleOwner) {
            (parentFragment as? AddressSearchListener)?.onReceive(it)
            (activity as? AddressSearchListener)?.onReceive(it)
            onBack()
        }
        viewModel.addressSearchKeyword.observe(viewLifecycleOwner) {
            if (it.isEmpty()) viewModel.clearAddresses()
        }
    }

    override fun onBack() {
        parentFragmentManager.popBackStack()
    }
}
