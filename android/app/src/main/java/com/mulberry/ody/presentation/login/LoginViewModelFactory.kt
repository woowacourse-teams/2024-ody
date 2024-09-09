package com.mulberry.ody.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mulberry.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.mulberry.ody.domain.repository.ody.AuthTokenRepository

class LoginViewModelFactory(
    private val authTokenRepository: AuthTokenRepository,
    private val loginRepository: KakaoLoginRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(authTokenRepository, loginRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}