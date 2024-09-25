package com.mulberry.ody.presentation.address.listener

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemAddressSearchBinding
import com.mulberry.ody.presentation.address.model.AddressUiModel

class AddressViewHolder(private val binding: ItemAddressSearchBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        addressUiModel: AddressUiModel,
        addressListener: AddressListener,
    ) {
        binding.address = addressUiModel
        binding.locationListener = addressListener
    }
}
