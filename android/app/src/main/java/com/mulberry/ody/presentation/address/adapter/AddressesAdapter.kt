package com.mulberry.ody.presentation.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mulberry.ody.databinding.ItemAddressSearchBinding
import com.mulberry.ody.presentation.address.listener.AddressListener
import com.mulberry.ody.presentation.address.listener.AddressViewHolder
import com.mulberry.ody.presentation.address.model.AddressUiModel

class AddressesAdapter(
    private val addressListener: AddressListener,
) : ListAdapter<AddressUiModel, AddressViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AddressViewHolder {
        val binding = ItemAddressSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AddressViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), addressListener)
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<AddressUiModel>() {
                override fun areItemsTheSame(
                    oldItem: AddressUiModel,
                    newItem: AddressUiModel,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: AddressUiModel,
                    newItem: AddressUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
