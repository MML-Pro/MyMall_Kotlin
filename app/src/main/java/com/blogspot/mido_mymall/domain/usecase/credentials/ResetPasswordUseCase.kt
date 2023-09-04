package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(email:String) = credentialsRepository.resetPassword(email)
}