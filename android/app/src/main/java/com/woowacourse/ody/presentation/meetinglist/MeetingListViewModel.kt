package com.woowacourse.ody.presentation.meetinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel
import timber.log.Timber
import java.time.LocalDateTime

class MeetingListViewModel : ViewModel() {
    private val _meetingList = MutableLiveData<List<MeetingUiModel>>()
    val meetingList: LiveData<List<MeetingUiModel>> = _meetingList

    private val fakeMeetingUiModel =
        MeetingUiModel(
            id = 0,
            title = "약속이름이너무너무너무너무너무너무너무너무길어요",
            datetime = LocalDateTime.of(2024, 8, 6, 20, 30),
            departure = "서울 송파구 올림픽로 35다길",
            arrival = "서울 강남구 테헤란로 411길",
            eta = "30",
        )
    private val fakeMeetingList =
        listOf(
            fakeMeetingUiModel,
            fakeMeetingUiModel.copy(
                id = 1,
                datetime = LocalDateTime.of(2024, 8, 7, 8, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 2,
                datetime = LocalDateTime.of(2024, 8, 8, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 3,
                datetime = LocalDateTime.of(2024, 8, 9, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 4,
                datetime = LocalDateTime.of(2024, 8, 10, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 5,
                datetime = LocalDateTime.of(2024, 8, 11, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 6,
                datetime = LocalDateTime.of(2024, 8, 12, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 7,
                datetime = LocalDateTime.of(2024, 8, 13, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 8,
                datetime = LocalDateTime.of(2024, 8, 14, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 9,
                datetime = LocalDateTime.of(2024, 8, 15, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 10,
                datetime = LocalDateTime.of(2024, 8, 16, 20, 30),
            ),
            fakeMeetingUiModel.copy(
                id = 11,
                datetime = LocalDateTime.of(2024, 8, 17, 20, 30),
            ),
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
    }

    fun navigateToMeetingRoom(position: Int) {
        Timber.d("$position")
    }
}
