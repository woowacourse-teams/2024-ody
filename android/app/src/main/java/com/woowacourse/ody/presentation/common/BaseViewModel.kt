package com.woowacourse.ody.presentation.common

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    private val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    private val _errorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<Unit> get() = _errorEvent

    protected var lastFailedAction: (() -> Unit)? = null

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    fun handleNetworkError() {
        _networkErrorEvent.setValue(Unit)
    }

    fun handleError() {
        _errorEvent.setValue(Unit)
    }
}
