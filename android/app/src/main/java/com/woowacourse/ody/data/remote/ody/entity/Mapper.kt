package com.woowacourse.ody.data.remote.ody.entity

import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MateResponse
import com.woowacourse.ody.data.remote.ody.entity.meeting.response.MeetingResponse
import com.woowacourse.ody.data.remote.ody.entity.notification.response.NotificationLogsResponse
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.model.NotificationType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun NotificationLogsResponse.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        val type = NotificationType.from(it.type)
        val nickname = it.nickname
        val createdAt = it.createdAt.parseToLocalDateTime()
        NotificationLog(type, nickname, createdAt)
    }

fun MeetingResponse.toMeeting(): Meeting =
    Meeting(
        this.id,
        this.name,
        this.targetAddress,
        this.date.parseToLocalDate(),
        this.time.parseToLocalTime(),
        this.mates.map { it.toMate() },
        this.inviteCode,
    )

fun MateResponse.toMate(): Mate = Mate(this.nickname)

fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    return LocalDateTime.parse(this, formatter)
}

fun String.parseToLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

fun String.parseToLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return LocalTime.parse(this, formatter)
}
