package com.mulberry.ody.presentation.feature.setting

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.repository.ody.SettingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.feature.login.LoginNavigatedReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val authRepository: AuthRepository,
        private val matesEtaRepository: MatesEtaRepository,
        private val settingRepository: SettingRepository,
    ) : BaseViewModel() {
        private val _loginNavigateEvent: MutableSharedFlow<LoginNavigatedReason> = MutableSharedFlow()
        val loginNavigateEvent: SharedFlow<LoginNavigatedReason> get() = _loginNavigateEvent.asSharedFlow()

        private val _isDepartureNotificationOn: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val isDepartureNotificationOn: StateFlow<Boolean> get() = _isDepartureNotificationOn.asStateFlow()

        private val _isEntryNotificationOn: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val isEntryNotificationOn: StateFlow<Boolean> get() = _isEntryNotificationOn.asStateFlow()

        fun fetchNotificationSetting() {
            viewModelScope.launch {
                _isDepartureNotificationOn.value = settingRepository.isNotificationOn(NotificationType.DEPARTURE_REMINDER).first()
                _isEntryNotificationOn.value = settingRepository.isNotificationOn(NotificationType.ENTRY).first()
            }
        }

        fun changeDepartureNotification(isOn: Boolean) {
            viewModelScope.launch {
                settingRepository.changeNotificationSetting(NotificationType.DEPARTURE_REMINDER, isOn)
                _isDepartureNotificationOn.value = isOn
            }
        }

        fun changeEntryNotification(isOn: Boolean) {
            viewModelScope.launch {
                settingRepository.changeNotificationSetting(NotificationType.ENTRY, isOn)
                _isEntryNotificationOn.value = isOn
            }
        }

        fun logout() {
            viewModelScope.launch {
                authRepository.logout()
                _loginNavigateEvent.emit(LoginNavigatedReason.LOGOUT)
                matesEtaRepository.clearEtaReservation(isReservationPending = true)
            }
        }

        fun withdraw() {
            viewModelScope.launch {
                startLoading()
                authRepository.withdrawAccount()
                    .onSuccess {
                        _loginNavigateEvent.emit(LoginNavigatedReason.WITHDRAWAL)
                        matesEtaRepository.clearEtaFetchingJob()
                        matesEtaRepository.clearEtaReservation(isReservationPending = false)
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { withdraw() }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "SettingViewModel"
        }
    }
