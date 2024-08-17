package com.woowacourse.ody.presentation.meetings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.apiresult.onUnexpected
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.meetings.listener.MeetingsItemListener
import com.woowacourse.ody.presentation.meetings.model.MeetingUiModel
import com.woowacourse.ody.presentation.meetings.model.toMeetingCatalogUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingsViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : ViewModel(), MeetingsItemListener {
    private val _meetingCatalogs = MutableLiveData<List<MeetingUiModel>>()
    val meetingCatalogs: LiveData<List<MeetingUiModel>> = _meetingCatalogs

    private val _navigateAction = MutableSingleLiveData<MeetingsNavigateAction>()
    val navigateAction: SingleLiveData<MeetingsNavigateAction> = _navigateAction

    val isMeetingCatalogsEmpty: LiveData<Boolean> = _meetingCatalogs.map { it.isEmpty() }

    fun fetchMeetingCatalogs() =
        viewModelScope.launch {
            meetingRepository.fetchMeetingCatalogs2()
                .onSuccess {
                    _meetingCatalogs.value = it.toMeetingCatalogUiModels()
                }.onFailure { code, errorMessage ->
                    analyticsHelper.logNetworkErrorEvent(TAG, errorMessage)
                    Timber.e("code: $code, message: $errorMessage")
                }.onNetworkError { exception ->
                    Timber.e(exception)
                }.onUnexpected { t ->
                    Timber.e(t)
                }
        }

    override fun navigateToEtaDashboard(meetingId: Long) {
        _navigateAction.postValue(MeetingsNavigateAction.NavigateToEtaDashboard(meetingId))
    }

    override fun navigateToNotificationLog(meetingId: Long) {
        _navigateAction.postValue(MeetingsNavigateAction.NavigateToNotificationLog(meetingId))
    }

    override fun toggleFold(position: Int) {
        val currentList = _meetingCatalogs.value ?: emptyList()
        val newList = currentList.toMutableList()
        newList[position] =
            newList[position].copy(isFolded = !newList[position].isFolded)
        _meetingCatalogs.value = newList
    }

    companion object {
        private const val TAG = "MeetingsViewModel"
    }
}
