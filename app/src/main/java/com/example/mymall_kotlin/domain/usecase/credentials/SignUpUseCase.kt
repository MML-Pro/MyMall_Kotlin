package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.models.User
import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(user:User, password:String) =
        credentialsRepository.createUserWithEmailAndPassword(user, password)

}