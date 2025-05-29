package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import javax.inject.Inject

class CheckInviteCodeUseCase @Inject constructor(private val meetingRepository: MeetingRepository) {
    suspend operator fun invoke(inviteCode: String): ApiResult<Unit> {
        return meetingRepository.fetchInviteCodeValidity(inviteCode)
    }
}
