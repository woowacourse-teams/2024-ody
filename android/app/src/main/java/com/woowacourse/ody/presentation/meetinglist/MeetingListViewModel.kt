package com.woowacourse.ody.presentation.meetinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel
import timber.log.Timber

class MeetingListViewModel : ViewModel() {
    private val _meetingList = MutableLiveData<List<MeetingUiModel>>()
    val meetingList: LiveData<List<MeetingUiModel>> = _meetingList

    private val fakeMeetingUiModel =
        MeetingUiModel(
            id = 0,
            title = "아침먹기에도점심먹기에도애매한시간식사약속",
            datetime = "2024-01-01 10:00",
            departure = "우리집",
            arrival = "너희집",
            eta = "30",
        )
    private val fakeMeetingList =
        listOf(
            fakeMeetingUiModel,
            fakeMeetingUiModel.copy(id = 1),
            fakeMeetingUiModel.copy(id = 2),
            fakeMeetingUiModel.copy(id = 3),
            fakeMeetingUiModel.copy(id = 4),
            fakeMeetingUiModel.copy(id = 5),
            fakeMeetingUiModel.copy(id = 6),
            fakeMeetingUiModel.copy(id = 7),
        )

    init {
        _meetingList.value = fakeMeetingList
    }

    fun toggleFold(position: Int) {
        val currentList = _meetingList.value ?: emptyList()
        val newList = currentList.toMutableList()
        newList[position] =
            newList[position].copy(isFolded = !newList[position].isFolded)
        _meetingList.value = newList
        Timber.d("$position")
    }

    fun navigateToMeetingRoom(position: Int) {
    }
}
