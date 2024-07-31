package com.woowacourse.ody.domain.repository.ody

interface InviteCodeRepository {
    suspend fun fetchInviteCode(): Result<String>

    suspend fun postInviteCode(inviteCode: String)
}
