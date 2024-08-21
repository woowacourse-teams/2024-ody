package com.woowacourse.ody.presentation.login

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.repository.ody.AuthTokenRepository
import com.woowacourse.ody.presentation.common.BaseViewModel
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authTokenRepository: AuthTokenRepository,
    private val kakaoLoginRepository: KakaoLoginRepository,
) : BaseViewModel() {
    private val _navigateAction: MutableSingleLiveData<LoginNavigateAction> =
        MutableSingleLiveData()
    val navigateAction: SingleLiveData<LoginNavigateAction> get() = _navigateAction

    fun checkIfLogined() {
        viewModelScope.launch {
            authTokenRepository.fetchAuthToken().onSuccess {
                if (kakaoLoginRepository.checkIfLogined() && it.accessToken.isNotEmpty()) {
                    navigateToMeetings()
                }
            }
        }
    }

    fun loginWithKakao(context: Context) =
        viewModelScope.launch {
            kakaoLoginRepository.login(context)
                .onSuccess {
                    navigateToMeetings()
                }.onFailure { code, errorMessage ->
                    handleError()
                }.onNetworkError {
                    handleNetworkError()
                }
        }

    private fun navigateToMeetings() {
        _navigateAction.setValue(LoginNavigateAction.NavigateToMeetings)
    }
}
