package com.ydo.ody.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
) {
    override fun toString(): String = "access-token=$accessToken refresh-token=$refreshToken"
}
