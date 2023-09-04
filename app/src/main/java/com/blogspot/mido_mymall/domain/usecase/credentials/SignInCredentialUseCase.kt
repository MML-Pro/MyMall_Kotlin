package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class SignInCredentialUseCase @Inject
constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(credential: AuthCredential) =
        credentialsRepository.signInWithCredential(credential)
}