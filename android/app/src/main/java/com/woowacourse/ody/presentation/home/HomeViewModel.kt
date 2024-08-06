package com.woowacourse.ody.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.home.model.MeetingCatalogUiModel
import com.woowacourse.ody.presentation.home.model.toMeetingCatalogUiModels
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    private val _meetingCatalogs = MutableLiveData<List<MeetingCatalogUiModel>>()
    val meetingCatalogs: LiveData<List<MeetingCatalogUiModel>> = _meetingCatalogs

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

    fun toggleFold(position: Int) {
        val currentList = _meetingCatalogs.value ?: emptyList()
        val newList = currentList.toMutableList()
        newList[position] =
            newList[position].copy(isFolded = !newList[position].isFolded)
        _meetingCatalogs.value = newList
    }
}
