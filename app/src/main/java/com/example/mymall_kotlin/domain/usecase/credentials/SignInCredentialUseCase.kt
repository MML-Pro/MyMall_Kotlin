package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class SignInCredentialUseCase @Inject
constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(credential: AuthCredential) =
        credentialsRepository.signInWithCredential(credential)
}