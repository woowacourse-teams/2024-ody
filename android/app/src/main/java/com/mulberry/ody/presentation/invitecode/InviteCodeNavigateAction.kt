package com.mulberry.ody.presentation.invitecode

sealed interface InviteCodeNavigateAction {
    data object CodeNavigateToJoin : InviteCodeNavigateAction
}
