package com.mulberry.ody.presentation.meetings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.meetings.listener.MeetingsItemListener
import com.mulberry.ody.presentation.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.meetings.model.toMeetingCatalogUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MeetingsViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
    ) : BaseViewModel(), MeetingsItemListener {
        private val _meetingCatalogs = MutableLiveData<List<MeetingUiModel>>()
        val meetingCatalogs: LiveData<List<MeetingUiModel>> get() = _meetingCatalogs

        private val _navigateAction = MutableSingleLiveData<MeetingsNavigateAction>()
        val navigateAction: SingleLiveData<MeetingsNavigateAction> = _navigateAction

        private val _isMeetingCatalogsEmpty: MutableLiveData<Boolean> = MutableLiveData()
        val isMeetingCatalogsEmpty: LiveData<Boolean> get() = _isMeetingCatalogsEmpty

        fun fetchMeetingCatalogs() {
            viewModelScope.launch {
                startLoading()
                meetingRepository.fetchMeetingCatalogs()
                    .onSuccess {
                        val meetingCatalogs = it.toMeetingCatalogUiModels()
                        _meetingCatalogs.value = meetingCatalogs
                        _isMeetingCatalogsEmpty.value = meetingCatalogs.isEmpty()
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { fetchMeetingCatalogs() }
                    }
                stopLoading()
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
