package com.mulberry.ody.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.repository.ody.LoginRepository
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.login.LoginNavigatedReason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
    @Inject
    constructor(
        private val loginRepository: LoginRepository,
    ) : ViewModel() {
        private val _loginNavigateEvent: MutableSingleLiveData<LoginNavigatedReason> = MutableSingleLiveData()
        val loginNavigateEvent: SingleLiveData<LoginNavigatedReason> get() = _loginNavigateEvent

        fun kakaoLogout() =
            viewModelScope.launch {
                loginRepository.logout()
                _loginNavigateEvent.setValue(LoginNavigatedReason.LOGOUT)
            }
    }
