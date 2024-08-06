package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCatalog
import com.woowacourse.ody.domain.model.MeetingCreationInfo
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

    override suspend fun postMeeting(meetingCreationInfo: MeetingCreationInfo): Result<String> = Result.success("")

    override suspend fun patchMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        currentLatitude: String,
        currentLongitude: String,
    ): Result<List<MateEta>> {
        return Result.success(
            listOf(
                MateEta("mateA", EtaType.LATE, 1L),
                MateEta("mateB", EtaType.LATE, 1L),
                MateEta("mateC", EtaType.LATE, 1L),
            ),
        )
    }

    override suspend fun fetchMeetingCatalogs(): Result<List<MeetingCatalog>> = Result.success(listOf())

    override suspend fun fetchMeeting(meetingId: Long): Result<Meeting> = Result.success(meetingA)
}
