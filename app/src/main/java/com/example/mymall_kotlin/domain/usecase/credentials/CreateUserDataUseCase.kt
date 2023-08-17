package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import javax.inject.Inject

class CreateUserDataUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    suspend operator fun invoke() = credentialsRepository.createUserData()
}