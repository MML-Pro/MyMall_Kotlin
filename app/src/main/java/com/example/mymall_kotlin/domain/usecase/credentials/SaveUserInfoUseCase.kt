package com.example.mymall_kotlin.domain.usecase.credentials

import com.example.mymall_kotlin.domain.repo.CredentialsRepository
import javax.inject.Inject

class SaveUserInfoUseCase @Inject constructor(private val credentialsRepository: CredentialsRepository) {

    operator fun invoke(uID: String, userName: String, email: String) =
        credentialsRepository.saveUserInfo(uID,userName, email)
}