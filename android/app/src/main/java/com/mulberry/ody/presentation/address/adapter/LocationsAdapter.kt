package com.mulberry.ody.presentation.address.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mulberry.ody.databinding.ItemAddressSearchBinding
import com.mulberry.ody.presentation.address.listener.LocationListener
import com.mulberry.ody.presentation.address.listener.LocationViewHolder
import com.mulberry.ody.presentation.address.model.AddressUiModel

class LocationsAdapter(
    private val locationListener: LocationListener,
) : ListAdapter<AddressUiModel, LocationViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): LocationViewHolder {
        val binding = ItemAddressSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LocationViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), locationListener)
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
