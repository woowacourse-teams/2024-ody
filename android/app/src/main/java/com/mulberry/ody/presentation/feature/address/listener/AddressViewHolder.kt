package com.mulberry.ody.presentation.feature.address.listener

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemAddressSearchBinding
import com.mulberry.ody.domain.model.Address

class AddressViewHolder(private val binding: ItemAddressSearchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        address: Address,
        addressListener: AddressListener,
    ) {
        binding.address = address
        binding.addressListener = addressListener
    }
}
