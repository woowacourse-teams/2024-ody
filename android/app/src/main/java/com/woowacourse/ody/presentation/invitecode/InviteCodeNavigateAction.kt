package com.woowacourse.ody.presentation.invitecode

sealed interface InviteCodeNavigateAction {
    object CodeNavigateToNotificationLog : InviteCodeNavigateAction
}
