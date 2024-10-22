package com.mulberry.ody.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    private val _networkErrorEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
    val networkErrorEvent: SharedFlow<Unit> get() = _networkErrorEvent.asSharedFlow()

    private val _errorEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
    val errorEvent: SharedFlow<Unit> get() = _errorEvent.asSharedFlow()

    protected var lastFailedAction: (() -> Unit)? = null

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    fun handleNetworkError() {
        viewModelScope.launch {
            _networkErrorEvent.emit(Unit)
        }
    }

    fun handleError() {
        viewModelScope.launch {
            _errorEvent.emit(Unit)
        }
    }

    fun startLoading() {
        _isLoading.value = true
    }

    fun stopLoading() {
        _isLoading.value = false
    }
}
