package com.mulberry.ody

import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.Addresses
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.domain.model.NotificationLog
import com.mulberry.ody.domain.model.NotificationLogType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val inviteCode: String = "MDAxMQzv"

val meetingId: Long = 0L

val detailMeeting: DetailMeeting =
    DetailMeeting(
        id = meetingId,
        name = "meetingA",
        destinationAddress = "선릉 캠퍼스",
        departureAddress = "잠실 캠퍼스",
        date = LocalDate.of(2024, 1, 1),
        time = LocalTime.of(10, 0),
        departureTime = LocalTime.of(2, 30),
        durationTime = 30,
        mates = listOf(Mate("A", ""), Mate("B", ""), Mate("C", "")),
        inviteCode = inviteCode,
    )

val meeting =
    Meeting(
        meetingId,
        "meetingA",
        1,
        LocalDateTime.of(2024, 1, 1, 10, 0),
        "서울 강남구 테헤란로 411",
        "서울 송파구 올림픽로35다길 42",
        16,
    )

val meetings: List<Meeting> =
    listOf(
        meeting,
    )

val notificationLogs: List<NotificationLog> =
    listOf(
        NotificationLog(
            NotificationLogType.ENTRY,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 30),
            "",
        ),
        NotificationLog(
            NotificationLogType.ENTRY,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 31),
            "",
        ),
        NotificationLog(
            NotificationLogType.ENTRY,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 32),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE_REMINDER,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 33),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE_REMINDER,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 34),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE_REMINDER,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 35),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 36),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 37),
            "",
        ),
        NotificationLog(
            NotificationLogType.DEPARTURE,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 38),
            "",
        ),
    )

private val nicknames = listOf("콜리", "올리브", "카키", "해음")
private val mateEtaStatuses =
    listOf(EtaStatus.LateWarning(15), EtaStatus.ArrivalSoon(5), EtaStatus.Arrived, EtaStatus.Missing)

val mateEtaInfo =
    MateEtaInfo(
        userId = 3L,
        nicknames.mapIndexed { idx, nickname ->
            MateEta(idx.toLong(), nickname, mateEtaStatuses[idx])
        },
    )

fun Address(
    id: Long,
    roadNameAddress: String,
): Address {
    return Address(id = id, detailAddress = roadNameAddress, placeName = "", latitude = "0.0", longitude = "0.0")
}

val address = Address(id = 0L, roadNameAddress = "")

val addresses: Addresses =
    Addresses(
        addresses = List(5) { Address(id = it.toLong(), roadNameAddress = "") },
        isEnd = true,
    )
