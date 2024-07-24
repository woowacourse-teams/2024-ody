package com.woowacourse.ody.presentation.notificationlog.uimodel

import com.woowacourse.ody.domain.Meeting
import com.woowacourse.ody.domain.NotificationLog
import java.time.format.DateTimeFormatter

fun NotificationLog.toNotificationUiModel(): NotificationLogUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return NotificationLogUiModel(
        this.type,
        this.nickname,
        this.createdAt.format(dateTimeFormatter),
    )
}

fun List<NotificationLog>.toNotificationUiModels(): List<NotificationLogUiModel> = map { it.toNotificationUiModel() }

fun Meeting.toMeetingUiModel(): MeetingUiModel {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return MeetingUiModel(
        this.name,
        this.targetPosition,
        this.meetingTime.format(dateTimeFormatter),
        this.mates.map { it.nickname },
    )
}
