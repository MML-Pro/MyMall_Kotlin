package com.example.mymall_kotlin.domain.usecase.main_activity

import com.example.mymall_kotlin.domain.repo.MainActivityRepo
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(private val mainActivityRepo: MainActivityRepo) {

    suspend operator fun invoke() = mainActivityRepo.getUserInfo()
}