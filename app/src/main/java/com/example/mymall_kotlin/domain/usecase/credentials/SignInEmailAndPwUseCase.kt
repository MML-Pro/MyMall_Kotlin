package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignInEmailAndPwUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(email: String, password: String) =
        credentialsRepository.signInWithEmailAndPassword(email, password)
}