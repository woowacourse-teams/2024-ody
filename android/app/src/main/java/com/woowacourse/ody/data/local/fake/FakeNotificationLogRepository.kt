package com.woowacourse.ody.data.local.fake

import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationLogRepository
import com.woowacourse.ody.domain.NotificationType
import java.time.LocalDateTime

object FakeNotificationLogRepository : NotificationLogRepository {
    val entryALog =
        NotificationLog(
            NotificationType.ENTRY,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 30),
        )
    val entryBLog =
        NotificationLog(
            NotificationType.ENTRY,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 31),
        )
    val entryCLog =
        NotificationLog(
            NotificationType.ENTRY,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 32),
        )
    val departureReminderALog =
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 33),
        )
    val departureReminderBLog =
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 34),
        )
    val departureReminderCLog =
        NotificationLog(
            NotificationType.DEPARTURE_REMINDER,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 35),
        )
    val departureALog =
        NotificationLog(
            NotificationType.DEPARTURE,
            "A",
            LocalDateTime.of(2024, 7, 7, 14, 36),
        )
    val departureBLog =
        NotificationLog(
            NotificationType.DEPARTURE,
            "B",
            LocalDateTime.of(2024, 7, 7, 14, 37),
        )
    val departureCLog =
        NotificationLog(
            NotificationType.DEPARTURE,
            "C",
            LocalDateTime.of(2024, 7, 7, 14, 38),
        )

    override suspend fun fetchNotificationLogs(meetingId: Int): List<NotificationLog> =
        listOf(
            entryALog,
            entryBLog,
            entryCLog,
            departureReminderALog,
            departureReminderBLog,
            departureReminderCLog,
            departureALog,
            departureBLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
            departureCLog,
        )
}
