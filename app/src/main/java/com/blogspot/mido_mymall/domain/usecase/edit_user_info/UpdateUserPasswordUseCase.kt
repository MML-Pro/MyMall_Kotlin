package com.blogspot.mido_mymall.domain.usecase.edit_user_info

import com.blogspot.mido_mymall.domain.repo.UpdateUserInfoRepo
import javax.inject.Inject

class UpdateUserPasswordUseCase @Inject constructor(private val updateUserInfoRepo: UpdateUserInfoRepo) {

    suspend operator fun invoke(
        currentEmail: String,
        oldPassword: String,
        newPassword: String
    ) = updateUserInfoRepo.updatePassword(currentEmail, oldPassword, newPassword)
}