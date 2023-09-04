package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignInGoogleUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke() = credentialsRepository.signInWithGoogle()

}