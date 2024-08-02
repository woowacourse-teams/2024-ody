package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import kotlinx.coroutines.flow.first

class DefaultInviteCodeRepository(
    private val odyDatastore: OdyDatastore,
) : InviteCodeRepository {
    override suspend fun fetchInviteCode(): Result<String> = odyDatastore.getInviteCode().first()

    override suspend fun postInviteCode(inviteCode: String) = odyDatastore.setInviteCode(inviteCode)
}
