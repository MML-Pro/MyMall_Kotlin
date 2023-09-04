package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignInEmailAndPwUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(email: String, password: String) =
        credentialsRepository.signInWithEmailAndPassword(email, password)
}