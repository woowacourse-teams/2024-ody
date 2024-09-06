package com.mulberry.ody.data.remote.core.repository

import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.repository.ody.FCMTokenRepository
import kotlinx.coroutines.flow.first

class DefaultFCMTokenRepository(
    private val odyDatastore: OdyDatastore,
) : FCMTokenRepository {
    override suspend fun fetchFCMToken(): Result<String> = odyDatastore.getFCMToken().first()

    override suspend fun postFCMToken(fcmToken: String) = odyDatastore.setFCMToken(fcmToken)
}
