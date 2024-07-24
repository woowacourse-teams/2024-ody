package com.woowacourse.ody.data.local.fake

import com.woowacourse.ody.domain.Mate
import com.woowacourse.ody.domain.Meeting
import com.woowacourse.ody.domain.repository.MeetingRepository
import java.time.LocalDateTime

object FakeMeetingRepository : MeetingRepository {
    private val mateA: Mate = Mate("A")
    private val mateB: Mate = Mate("B")
    private val mateC: Mate = Mate("C")
    private val mates: List<Mate> = listOf(mateA, mateB, mateC)

    private val meetingA: Meeting =
        Meeting(
            0,
            "meetingA",
            "선릉 캠퍼스",
            LocalDateTime.of(2024, 1, 1, 10, 0, 0),
            mates,
        )

    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> = Result.success(Unit)

    override suspend fun fetchMeeting(): Result<List<Meeting>> = Result.success(listOf(meetingA))
}
