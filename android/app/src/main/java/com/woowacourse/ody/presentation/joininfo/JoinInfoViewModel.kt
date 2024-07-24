package com.woowacourse.ody.presentation.joininfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressValidator

class JoinInfoViewModel : ViewModel() {
    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }
    val hasNickname: LiveData<Boolean> = nickname.map { it.isNotEmpty() }

    private val _startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val startingPointGeoLocation: LiveData<GeoLocation> get() = _startingPointGeoLocation

    val isValidStartingPoint: LiveData<Boolean> = _startingPointGeoLocation.map { AddressValidator.isValid(it.address) }

    fun emptyNickname() {
        nickname.value = ""
    }

    fun setStartingPointGeoLocation(geoLocation: GeoLocation) {
        _startingPointGeoLocation.value = geoLocation
    }

    companion object {
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
