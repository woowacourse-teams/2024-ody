package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.domain.Meeting

interface MeetingRepository {
    suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit>

    suspend fun fetchMeeting(): Result<List<Meeting>>
}
