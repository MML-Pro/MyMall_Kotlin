package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(googleSignInClient: GoogleSignInClient?) =
        credentialsRepository.signOut(googleSignInClient)
}