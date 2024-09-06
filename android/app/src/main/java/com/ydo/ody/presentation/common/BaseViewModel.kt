package com.ydo.ody.presentation.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    private val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    private val _errorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<Unit> get() = _errorEvent

    protected var lastFailedAction: (() -> Unit)? = null

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    fun handleNetworkError() {
        _networkErrorEvent.setValue(Unit)
    }

    fun handleError() {
        _errorEvent.setValue(Unit)
    }

    fun startLoading() {
        _isLoading.value = true
    }

    fun stopLoading() {
        _isLoading.value = false
    }
}
