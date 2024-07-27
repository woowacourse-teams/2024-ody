package com.woowacourse.ody.presentation.completion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.data.remote.ody.entity.join.request.JoinRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.ody.entity.toMeeting
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class JoinCompleteViewModel(
    private val meetingRepository: MeetingRepository,
    private val joinRepository: JoinRepository,
    private val datastore: OdyDatastore,
) : ViewModel() {
    val meetingResponse: MutableLiveData<Meeting?> = MutableLiveData(null)

    private val _navigateAction: MutableLiveData<Event<Unit>> = MutableLiveData()
    val navigateAction: LiveData<Event<Unit>> get() = _navigateAction

    fun postMeeting(meetingRequest: MeetingRequest) {
        viewModelScope.launch {
            meetingRepository.postMeeting(meetingRequest)
                .onSuccess {
                    datastore.setInviteCode(it.inviteCode)
                    meetingResponse.value = it.toMeeting()
                    delay(1500)
                    _navigateAction.emit(Unit)
                }.onFailure {
                    Timber.e(it.message)
                }
        }
    }

    fun postMates(joinRequest: JoinRequest) {
        viewModelScope.launch {
            joinRepository.postMates(joinRequest)
                .onSuccess {
                    datastore.setInviteCode(it.inviteCode)
                    meetingResponse.value = it.toMeeting()
                    delay(1500)
                    _navigateAction.emit(Unit)
                }.onFailure {
                    Timber.e(it.message)
                }
        }
    }
}
