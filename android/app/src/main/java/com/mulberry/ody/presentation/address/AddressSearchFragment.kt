package com.mulberry.ody.presentation.address

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentAddressSearchBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment

class AddressSearchFragment :
    BindingFragment<FragmentAddressSearchBinding>(R.layout.fragment_address_search) {
    private val viewModel by viewModels<AddressSearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
