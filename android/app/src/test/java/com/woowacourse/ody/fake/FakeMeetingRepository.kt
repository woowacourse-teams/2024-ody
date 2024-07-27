package com.woowacourse.ody.fake

import com.woowacourse.ody.data.remote.ody.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import java.time.LocalDate
import java.time.LocalTime

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
            LocalDate.of(2024, 1, 1),
            LocalTime.of(10, 0),
            mates,
            "abcd1234",
        )

    override suspend fun fetchInviteCodeValidity(inviteCode: String): Result<Unit> = Result.success(Unit)

    override suspend fun fetchMeeting(): Result<List<Meeting>> = Result.success(listOf(meetingA))

    override suspend fun postMeeting(meetingRequest: MeetingRequest): Result<MeetingResponse> =
        Result.success(
            MeetingResponse(
                0,
                "meetingA",
                "선릉 캠퍼스",
                "2024, 1, 1, 10, 0, 0",
                "A,B,C",
                "A,B,C",
                "A,B,C",
                1,
                listOf(),
                "",
            ),
        )
}
