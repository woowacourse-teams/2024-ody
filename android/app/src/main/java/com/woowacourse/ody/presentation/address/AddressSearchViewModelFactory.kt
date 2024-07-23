package com.woowacourse.ody.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.GeoLocationRepository
import kotlinx.coroutines.CoroutineScope

class AddressSearchViewModelFactory(
    private val coroutineScope: CoroutineScope,
    private val geoLocationRepository: GeoLocationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressSearchViewModel::class.java)) {
            return AddressSearchViewModel(coroutineScope, geoLocationRepository) as T
        }
        throw IllegalArgumentException()
    }
}
