package com.woowacourse.ody.data.remote

import com.woowacourse.ody.data.remote.service.MeetingService
import com.woowacourse.ody.domain.repository.MeetingRepository

class DefaultMeetingRepository : MeetingRepository {
    private val service = RetrofitClient.retrofit.create(MeetingService::class.java)

    override suspend fun getInviteCodeValidity(inviteCode: String):Result<Unit> {
        return runCatching { service.getInviteCodeValidity(inviteCode) }
    }
}
