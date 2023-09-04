package com.blogspot.mido_mymall.domain.usecase.edit_user_info

import com.blogspot.mido_mymall.domain.repo.UpdateUserInfoRepo
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(private val updateUserInfoRepo: UpdateUserInfoRepo) {

    suspend operator fun invoke(userName:String) = updateUserInfoRepo.updateUserName(userName)
}