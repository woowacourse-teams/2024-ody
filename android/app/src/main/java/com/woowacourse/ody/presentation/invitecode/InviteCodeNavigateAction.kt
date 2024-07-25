package com.woowacourse.ody.presentation.invitecode

sealed interface InviteCodeNavigateAction {
    data object CodeNavigateToJoin : InviteCodeNavigateAction
}
