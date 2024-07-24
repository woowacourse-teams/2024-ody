package com.woowacourse.ody.domain.repository

interface MeetingRepository {
    suspend fun getInviteCodeValidity(inviteCode: String): Result<Unit>
}
