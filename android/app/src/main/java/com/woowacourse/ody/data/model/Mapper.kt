package com.woowacourse.ody.data.model

import com.woowacourse.ody.data.model.meeting.MateResponse
import com.woowacourse.ody.data.model.meeting.MeetingResponse
import com.woowacourse.ody.data.model.notification.NotificationLogsResponse
import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.model.NotificationType
import java.time.LocalDateTime
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
        this.date,
        this.time,
        this.mates.map { it.toMate() },
        this.inviteCode,
    )

fun MateResponse.toMate(): Mate = Mate(this.nickname)

fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
