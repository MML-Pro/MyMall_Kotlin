package com.blogspot.mido_mymall.domain.usecase.edit_user_info

import com.blogspot.mido_mymall.domain.repo.UpdateUserInfoRepo
import javax.inject.Inject

class UpdateUserEmailUseCase @Inject constructor(private val updateUserInfoRepo: UpdateUserInfoRepo) {

    suspend operator fun invoke(
        oldEmail: String?,
        userEmail: String, password: String?,
        idToken: String?
    ) = updateUserInfoRepo.updateEmail(oldEmail, userEmail, password, idToken)
}