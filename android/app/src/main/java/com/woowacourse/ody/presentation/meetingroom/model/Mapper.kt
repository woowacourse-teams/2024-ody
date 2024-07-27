package com.woowacourse.ody.presentation.meetingroom.model

import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationLog.toNotificationUiModel(): NotificationLogUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return NotificationLogUiModel(
        this.type,
        this.nickname,
        this.createdAt.format(dateTimeFormatter),
    )
}

fun List<NotificationLog>.toNotificationUiModels(): List<NotificationLogUiModel> = map { it.toNotificationUiModel() }

fun Meeting.toMeetingUiModel(): MeetingUiModel {
    return MeetingUiModel(
        this.name,
        this.targetPosition,
        this.toMeetingDateTime(),
        this.mates.map { it.nickname },
        this.inviteCode,
    )
}

private fun Meeting.toMeetingDateTime(): String {
    val dateTime = LocalDateTime.of(meetingDate, meetingTime)
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
    return dateTime.format(dateTimeFormatter)
}
