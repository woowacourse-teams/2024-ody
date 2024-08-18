package com.woowacourse.ody.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.data.remote.core.repository.DefaultLoginRepository
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.apiresult.onUnexpected
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val loginRepository: DefaultLoginRepository,
) : ViewModel() {
    fun loginWithKakao() =
        viewModelScope.launch {
            loginRepository.loginWithKakao()
                .onSuccess {
                    Timber.e("success")
                }.onFailure { code, errorMessage ->
                    Timber.e(errorMessage)
                }.onNetworkError {
                    Timber.e(it)
                }.onUnexpected {
                    Timber.e(it)
                }
        }
}
