package com.example.mymall_kotlin.domain.usecase.edit_user_info

import com.example.mymall_kotlin.domain.repo.UpdateUserInfoRepo
import javax.inject.Inject

class UpdateUserEmailUseCase @Inject constructor(private val updateUserInfoRepo: UpdateUserInfoRepo) {

    suspend operator fun invoke(
        oldEmail: String?,
        userEmail: String, password: String?,
        idToken: String?
    ) = updateUserInfoRepo.updateEmail(oldEmail, userEmail, password, idToken)
}