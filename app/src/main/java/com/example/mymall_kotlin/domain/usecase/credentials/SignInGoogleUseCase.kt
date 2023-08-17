package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignInGoogleUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke() = credentialsRepository.signInWithGoogle()

}