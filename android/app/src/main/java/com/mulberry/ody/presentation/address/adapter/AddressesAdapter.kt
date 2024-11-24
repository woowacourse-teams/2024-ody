package com.mulberry.ody.presentation.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mulberry.ody.databinding.ItemAddressSearchBinding
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.address.listener.AddressListener
import com.mulberry.ody.presentation.address.listener.AddressViewHolder

class AddressesAdapter(
    private val addressListener: AddressListener,
) : PagingDataAdapter<Address, AddressViewHolder>(diffUtil) {
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
        val address = getItem(position) ?: return
        holder.bind(address, addressListener)
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<Address>() {
                override fun areItemsTheSame(
                    oldItem: Address,
                    newItem: Address,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: Address,
                    newItem: Address,
                ): Boolean = oldItem.id == newItem.id
            }
    }
}
