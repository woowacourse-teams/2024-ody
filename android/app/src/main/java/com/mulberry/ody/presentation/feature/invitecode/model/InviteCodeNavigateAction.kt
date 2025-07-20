package com.mulberry.ody.presentation.feature.invitecode.model

sealed interface InviteCodeNavigateAction {
    data object CodeNavigateToJoin : InviteCodeNavigateAction
}
