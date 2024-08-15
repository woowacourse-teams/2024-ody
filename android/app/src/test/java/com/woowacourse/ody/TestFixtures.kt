package com.woowacourse.ody

import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.model.NotificationType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

val inviteCode: String = "abc123"

val meetingId: Long = 0L

val meeting: Meeting =
    Meeting(
        0,
        "meetingA",
        "선릉 캠퍼스",
        LocalDate.of(2024, 1, 1),
        LocalTime.of(10, 0),
        listOf(Mate("A"), Mate("B"), Mate("C")),
        inviteCode,
    )

val notificationLogs: List<NotificationLog> =
    listOf(
        NotificationLog(
            NotificationType.ENTRY,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 30),
        ),
        NotificationLog(
            NotificationType.ENTRY,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 31),
        ),
        NotificationLog(
            NotificationType.ENTRY,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 32),
        ),
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 33),
        ),
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 34),
        ),
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 35),
        ),
        NotificationLog(
            NotificationType.DEPARTURE,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 36),
        ),
        NotificationLog(
            NotificationType.DEPARTURE,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 37),
        ),
        NotificationLog(
            NotificationType.DEPARTURE,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 38),
        ),
    )

private val userNickname = "해음"
private val nicknames = listOf("콜리", "올리브", "카키", "해음")
private val mateEtaTypes =
    listOf(EtaType.LATE_WARNING, EtaType.ARRIVAL_SOON, EtaType.ARRIVED, EtaType.MISSING)
val mateEtaDurationMinutes = listOf(83, 10, 0, -1)

val mateEtaInfo =
    MateEtaInfo(
        userNickname = userNickname,
        nicknames.mapIndexed { idx, nickname ->
            MateEta(nickname, mateEtaTypes[idx], mateEtaDurationMinutes[idx])
        },
    )
