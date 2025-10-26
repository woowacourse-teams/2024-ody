package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.repository.ody.AuthRepository
import javax.inject.Inject

class IsLoggedInUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(): Boolean {
            return authRepository.isLoggedIn()
        }
    }
