package com.blogspot.mido_mymall.domain.usecase.main_activity

import com.blogspot.mido_mymall.domain.repo.MainActivityRepo
import javax.inject.Inject

class UpdateLastSeenUseCase @Inject constructor(
    private val mainActivityRepo: MainActivityRepo
) {
    suspend operator fun invoke() = mainActivityRepo.updateUserLastSeen()
}