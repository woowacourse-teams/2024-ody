package com.woowacourse.ody.data.model

import com.woowacourse.ody.data.model.meeting.MateEntity
import com.woowacourse.ody.data.model.meeting.MeetingEntity
import com.woowacourse.ody.data.model.notification.NotificationLogEntities
import com.woowacourse.ody.domain.Mate
import com.woowacourse.ody.domain.Meeting
import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLogEntities.toNotificationList(): List<NotificationLog> =
    this.logList.map {
        val type = NotificationType.from(it.type)
        val nickname = it.nickname
        val createdAt = it.createdAt.parseToLocalDateTime()
        NotificationLog(type, nickname, createdAt)
    }

fun MeetingEntity.toMeeting(): Meeting =
    Meeting(
        this.id,
        this.name,
        this.targetAddress,
        this.date.parseToLocalDateTime(),
        this.mates.map { it.toMate() },
    )

fun MateEntity.toMate(): Mate = Mate(this.nickname)

fun String.parseToLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
    return LocalDateTime.parse(this, formatter)
}
