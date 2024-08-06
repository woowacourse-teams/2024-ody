package com.woowacourse.ody.presentation.meetinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.meetinglist.model.MeetingCatalogUiModel
import com.woowacourse.ody.presentation.meetinglist.model.toMeetingCatalogUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingsViewModel(
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val _meetingCatalogs = MutableLiveData<List<MeetingCatalogUiModel>>()
    val meetingCatalogs: LiveData<List<MeetingCatalogUiModel>> = _meetingCatalogs

    init {
        fetchMeetingCatalogs()
    }

    private fun fetchMeetingCatalogs() =
        viewModelScope.launch {
            meetingRepository.fetchMeetingCatalogs().onSuccess {
                _meetingCatalogs.value = it.toMeetingCatalogUiModels()
            }.onFailure {
                Timber.e(it)
            }
        }

    fun toggleFold(position: Int) {
        val currentList = _meetingCatalogs.value ?: emptyList()
        val newList = currentList.toMutableList()
        newList[position] =
            newList[position].copy(isFolded = !newList[position].isFolded)
        _meetingCatalogs.value = newList
    }

    fun navigateToMeetingRoom(position: Int) {
    }
}
