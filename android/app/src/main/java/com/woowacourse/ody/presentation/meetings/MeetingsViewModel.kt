package com.woowacourse.ody.presentation.meetings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.meetings.listener.MeetingsItemListener
import com.woowacourse.ody.presentation.meetings.model.MeetingUiModel
import com.woowacourse.ody.presentation.meetings.model.toMeetingCatalogUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingsViewModel(
    private val meetingRepository: MeetingRepository,
) : ViewModel(), MeetingsItemListener {
    private val _meetingCatalogs = MutableLiveData<List<MeetingUiModel>>()
    val meetingCatalogs: LiveData<List<MeetingUiModel>> = _meetingCatalogs

    private val _navigateAction = MutableSingleLiveData<MeetingsNavigateAction>()
    val navigateAction: SingleLiveData<MeetingsNavigateAction> = _navigateAction

    val isMeetingCatalogsEmpty: LiveData<Boolean> =
        _meetingCatalogs.map {
            it.isEmpty()
        }

    fun fetchMeetingCatalogs() =
        viewModelScope.launch {
            meetingRepository.fetchMeetingCatalogs().onSuccess {
                _meetingCatalogs.value = it.toMeetingCatalogUiModels()
            }.onFailure {
                Timber.e(it)
            }
        }

    override fun navigateToEta(meetingId: Long) {
        viewModelScope.launch {
            meetingRepository.fetchMeeting(meetingId).onSuccess {
                _navigateAction.postValue(
                    MeetingsNavigateAction.NavigateToEta(
                        meetingId = it.id,
                        inviteCode = it.inviteCode,
                        title = it.name,
                    ),
                )
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    override fun navigateToMeetingRoom(meetingId: Long) =
        _navigateAction.postValue(MeetingsNavigateAction.NavigateToNotificationLog(meetingId))

    override fun toggleFold(position: Int) {
        val currentList = _meetingCatalogs.value ?: emptyList()
        val newList = currentList.toMutableList()
        newList[position] =
            newList[position].copy(isFolded = !newList[position].isFolded)
        _meetingCatalogs.value = newList
    }
}
