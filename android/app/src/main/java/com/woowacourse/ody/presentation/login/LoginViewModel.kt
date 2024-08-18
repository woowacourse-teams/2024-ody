package com.woowacourse.ody.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.data.remote.thirdparty.login.DefaultLoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: DefaultLoginRepository,
) : ViewModel() {
    fun loginWithKakao() =
        viewModelScope.launch {
            loginRepository.loginWithKakao()
        }
}
