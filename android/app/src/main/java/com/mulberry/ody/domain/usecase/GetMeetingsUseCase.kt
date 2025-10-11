package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeetingsUseCase
@Inject
constructor(
    private val meetingRepository: MeetingRepository,
) {
    suspend operator fun invoke(): ApiResult<List<Meeting>> {
        return meetingRepository.fetchMeetings()
    }
}
