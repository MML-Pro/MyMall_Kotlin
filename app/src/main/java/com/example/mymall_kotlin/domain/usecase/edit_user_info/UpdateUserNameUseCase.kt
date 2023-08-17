package com.example.mymall_kotlin.domain.usecase.edit_user_info

import com.example.mymall_kotlin.domain.repo.UpdateUserInfoRepo
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(private val updateUserInfoRepo: UpdateUserInfoRepo) {

    suspend operator fun invoke(userName:String) = updateUserInfoRepo.updateUserName(userName)
}