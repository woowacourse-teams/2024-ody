package com.woowacourse.ody.presentation.meetinglist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel

class MeetingListViewModel : ViewModel() {
    private val _meetingList = MutableLiveData<List<MeetingUiModel>>()
    val meetingList: LiveData<List<MeetingUiModel>> = _meetingList
    private val _folded = MutableLiveData<List<Boolean>>()
    val folded = _folded

    private val fakeMeetingUiModel =
        MeetingUiModel(
            title = "아침먹기에도점심먹기에도애매한시간식사약속",
            datetime = "2024-01-01 10:00",
            departure = "우리집",
            arrival = "너희집",
            eta = "30",
        )
    private val fakeMeetingList =
        listOf(
            fakeMeetingUiModel,
            fakeMeetingUiModel,
            fakeMeetingUiModel,
            fakeMeetingUiModel,
            fakeMeetingUiModel,
            fakeMeetingUiModel,
            fakeMeetingUiModel,
        )

    init {
        _meetingList.value = fakeMeetingList
    }
}
