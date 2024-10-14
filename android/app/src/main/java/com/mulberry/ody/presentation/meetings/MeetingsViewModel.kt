package com.mulberry.ody.presentation.meetings

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.meetings.listener.MeetingsItemListener
import com.mulberry.ody.presentation.meetings.model.MeetingUiModel
import com.mulberry.ody.presentation.meetings.model.toMeetingCatalogUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
        private val _meetingCatalogs: MutableStateFlow<List<MeetingUiModel>> = MutableStateFlow(listOf())
        val meetingCatalogs: StateFlow<List<MeetingUiModel>> get() = _meetingCatalogs.asStateFlow()

        private val _navigateAction = MutableSharedFlow<MeetingsNavigateAction>()
        val navigateAction: SharedFlow<MeetingsNavigateAction> = _navigateAction.asSharedFlow()

        private val _isMeetingCatalogsEmpty: MutableStateFlow<Boolean> = MutableStateFlow(true)
        val isMeetingCatalogsEmpty: StateFlow<Boolean> get() = _isMeetingCatalogsEmpty.asStateFlow()

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
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToEtaDashboard(meetingId))
            }
        }

        override fun navigateToNotificationLog(meetingId: Long) {
            viewModelScope.launch {
                _navigateAction.emit(MeetingsNavigateAction.NavigateToNotificationLog(meetingId))
            }
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
