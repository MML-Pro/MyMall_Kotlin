package com.example.mymall_kotlin.domain.usecase.my_rewards

import com.example.mymall_kotlin.domain.repo.MainActivityRepo
import javax.inject.Inject

class GetUserLastSeenUseCase @Inject constructor(
    private val mainActivityRepo: MainActivityRepo
) {

    suspend operator fun invoke() = mainActivityRepo.getUserLastSeen()
}