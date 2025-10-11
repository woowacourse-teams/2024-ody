package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.NotificationType
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.repository.ody.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsValidInviteCodeUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository,
) {
    suspend operator fun invoke(inviteCode: String): ApiResult<Unit> {
        return meetingRepository.fetchInviteCodeValidity(inviteCode)
    }
}
