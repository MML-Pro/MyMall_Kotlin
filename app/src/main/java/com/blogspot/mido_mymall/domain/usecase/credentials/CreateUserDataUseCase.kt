package com.blogspot.mido_mymall.domain.usecase.credentials

import com.blogspot.mido_mymall.domain.repo.CredentialsRepository
import javax.inject.Inject

class CreateUserDataUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    suspend operator fun invoke() = credentialsRepository.createUserData()
}