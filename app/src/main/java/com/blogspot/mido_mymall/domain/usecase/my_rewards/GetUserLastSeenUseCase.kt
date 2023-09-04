package com.blogspot.mido_mymall.domain.usecase.my_rewards

import com.blogspot.mido_mymall.domain.repo.MainActivityRepo
import javax.inject.Inject

class GetUserLastSeenUseCase @Inject constructor(
    private val mainActivityRepo: MainActivityRepo
) {

    suspend operator fun invoke() = mainActivityRepo.getUserLastSeen()
}