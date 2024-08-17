package com.woowacourse.ody.presentation.common

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    protected val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    protected val _errorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<Unit> get() = _errorEvent

    protected var lastFailedAction: (() -> Unit)? = null

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }
}
