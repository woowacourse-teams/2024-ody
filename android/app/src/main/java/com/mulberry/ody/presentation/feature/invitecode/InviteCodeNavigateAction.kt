package com.mulberry.ody.presentation.feature.invitecode

sealed interface InviteCodeNavigateAction {
    data object CodeNavigateToJoin : InviteCodeNavigateAction
}
