package com.woowacourse.ody.presentation.joininfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressValidator
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit

class JoinInfoViewModel : ViewModel() {
    val isValidInfo: MediatorLiveData<Boolean> = MediatorLiveData()

    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }

    private val _startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val startingPointGeoLocation: LiveData<GeoLocation> get() = _startingPointGeoLocation

    private val _invalidStartingPointEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidStartingPointEvent: LiveData<Event<Unit>> get() = _invalidStartingPointEvent

    init {
        initializeIsValidInfo()
    }

    private fun initializeIsValidInfo() {
        isValidInfo.addSource(nickname) {
            isValidInfo.value = it.isNotEmpty()
        }
        isValidInfo.addSource(_startingPointGeoLocation) {
            val isValidStartingPoint = AddressValidator.isValid(it.address)
            isValidInfo.value = isValidStartingPoint
            if (!isValidStartingPoint) {
                _invalidStartingPointEvent.emit(Unit)
            }
        }
    }

    fun emptyNickname() {
        nickname.value = ""
    }

    fun setStartingPointGeoLocation(geoLocation: GeoLocation) {
        _startingPointGeoLocation.value = geoLocation
    }

    fun onNextInfo() {
        isValidInfo.value = false
    }

    companion object {
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
