package com.woowacourse.ody.presentation.destination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.woowacourse.ody.databinding.FragmentDestinationBinding
import com.woowacourse.ody.presentation.address.AddressSearchDialog

class DestinationFragment : Fragment() {
    private var _binding: FragmentDestinationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDestinationBinding.inflate(inflater, container, false)
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
        binding.etDestination.setOnClickListener {
            AddressSearchDialog().show(parentFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)
        }
        setFragmentResultListener(AddressSearchDialog.REQUEST_KEY) { _, bundle ->
            val address = bundle.getString(AddressSearchDialog.ADDRESS_KEY)
            binding.etDestination.setText(address)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
