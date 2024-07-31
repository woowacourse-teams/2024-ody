package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.domain.repository.ody.FCMTokenRepository
import kotlinx.coroutines.flow.first

class DefaultFCMTokenRepository(
    private val odyDatastore: OdyDatastore,
) : FCMTokenRepository {
    override suspend fun fetchFCMToken(): Result<String> = odyDatastore.getToken().first()

    override suspend fun postFCMToken(fcmToken: String) = odyDatastore.setToken(fcmToken)
}
