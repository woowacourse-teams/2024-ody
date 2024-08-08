package com.woowacourse.ody.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.woowacourse.ody.domain.repository.location.GeoLocationRepository

class AddressSearchViewModelFactory(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val geoLocationRepository: GeoLocationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressSearchViewModel::class.java)) {
            return AddressSearchViewModel(firebaseAnalytics, geoLocationRepository) as T
        }
        throw IllegalArgumentException()
    }
}
