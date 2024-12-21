package com.mulberry.ody.data.local.repository

import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.domain.repository.ody.FCMTokenRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DefaultFCMTokenRepository
@Inject
constructor(
    private val odyDatastore: OdyDatastore,
) : FCMTokenRepository {
    override suspend fun fetchFCMToken(): Result<String> {
        return odyDatastore.getFCMToken().first()
    }

    override suspend fun postFCMToken(fcmToken: String) = odyDatastore.setFCMToken(fcmToken)
}
