package com.mulberry.ody

import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.EtaType
import com.mulberry.ody.domain.model.LogType
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.domain.model.NotificationLog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val inviteCode: String = "MDAxMQzv"

val meetingId: Long = 0L

val meeting: Meeting =
    Meeting(
        0,
        "meetingA",
        "선릉 캠퍼스",
        LocalDate.of(2024, 1, 1),
        LocalTime.of(10, 0),
        listOf(Mate("A", ""), Mate("B", ""), Mate("C", "")),
        inviteCode,
    )

val meetings: List<Meeting> = listOf(meeting)

val meetingCatalog =
    MeetingCatalog(
        meetingId,
        "meetingA",
        1,
        LocalDateTime.of(2024, 1, 1, 10, 0),
        "서울 강남구 테헤란로 411",
        "서울 송파구 올림픽로35다길 42",
        16,
    )

val meetingCatalogs: List<MeetingCatalog> =
    listOf(
        meetingCatalog,
    )

val notificationLogs: List<NotificationLog> =
    listOf(
        NotificationLog(
            LogType.ENTRY,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 30),
            "",
        ),
        NotificationLog(
            LogType.ENTRY,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 31),
            "",
        ),
        NotificationLog(
            LogType.ENTRY,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 32),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE_REMINDER,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 33),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE_REMINDER,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 34),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE_REMINDER,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 35),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 36),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 37),
            "",
        ),
        NotificationLog(
            LogType.DEPARTURE,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 38),
            "",
        ),
    )

private val nicknames = listOf("콜리", "올리브", "카키", "해음")
private val mateEtaTypes =
    listOf(EtaType.LATE_WARNING, EtaType.ARRIVAL_SOON, EtaType.ARRIVED, EtaType.MISSING)
val mateEtaDurationMinutes = listOf(83, 10, 0, -1)

val mateEtaInfo =
    MateEtaInfo(
        userId = 3L,
        nicknames.mapIndexed { idx, nickname ->
            MateEta(idx.toLong(), nickname, mateEtaTypes[idx], mateEtaDurationMinutes[idx])
        },
    )

fun Address(id: Long, roadNameAddress: String): Address {
    return Address(id = id, detailAddress = roadNameAddress, placeName = "", latitude = "0.0", longitude = "0.0")
}

val addresses: List<Address> = List(5) { Address(id = it.toLong(), roadNameAddress = "") }
