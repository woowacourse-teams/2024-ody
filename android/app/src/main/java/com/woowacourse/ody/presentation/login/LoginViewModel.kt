package com.woowacourse.ody.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.data.remote.thirdparty.login.kakao.KakaoLoginRepository
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.apiresult.onUnexpected
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val kakaoLoginRepository: KakaoLoginRepository,
) : ViewModel() {
    private val _navigateAction: MutableSingleLiveData<LoginNavigateAction> = MutableSingleLiveData()
    val navigateAction: SingleLiveData<LoginNavigateAction> get() = _navigateAction

    fun loginWithKakao(context: Context) =
        viewModelScope.launch {
            kakaoLoginRepository.login(context)
                .onSuccess {
                    Timber.d("success")
                    navigateToMeetings()
                }.onFailure { code, errorMessage ->
                    Timber.e(errorMessage)
                }.onNetworkError {
                    Timber.e(it)
                }.onUnexpected {
                    Timber.e(it)
                }
        }

    private fun navigateToMeetings() {
        _navigateAction.setValue(LoginNavigateAction.NavigateToMeetings)
    }
}
