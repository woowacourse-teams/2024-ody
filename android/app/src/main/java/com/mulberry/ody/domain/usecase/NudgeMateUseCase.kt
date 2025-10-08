package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.NudgeInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.set

class NudgeMateUseCase @Inject constructor(
    private val meetingRepository: MeetingRepository,
) {
    private val matesNudgeTimes: MutableMap<Long, LocalDateTime> = mutableMapOf()

    suspend operator fun invoke(nudgeInfo: NudgeInfo): ApiResult<Unit> {
        val mateId = nudgeInfo.mateId
        val recentNudgeTime = matesNudgeTimes.getOrDefault(mateId, DEFAULT_NUDGE_TIME)
        val currentTime = LocalDateTime.now()
        val elapsedSeconds = Duration.between(recentNudgeTime, currentTime).seconds

        if (elapsedSeconds < NUDGE_DELAY_SECONDS) {
            val remainingTime = NUDGE_DELAY_SECONDS - elapsedSeconds
            return ApiResult.Unexpected(NudgeCooldownException(remainingTime))
        }

        matesNudgeTimes[mateId] = currentTime
        return meetingRepository.postNudge(nudgeInfo)
    }

    companion object {
        private const val NUDGE_DELAY_SECONDS = 10L
        private val DEFAULT_NUDGE_TIME = LocalDateTime.of(2000, 1, 1, 1, 1)
    }
}

class NudgeCooldownException(val remainingTime: Long) : Exception()
