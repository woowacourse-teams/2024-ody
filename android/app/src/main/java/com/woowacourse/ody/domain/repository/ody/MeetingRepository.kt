package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>

    suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String>

    suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<List<MateEta>>

    suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>>
}
