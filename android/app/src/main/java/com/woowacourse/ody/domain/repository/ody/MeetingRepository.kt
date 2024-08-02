package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCreationInfo

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>

    suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<Meeting>
}
