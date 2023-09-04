package com.blogspot.mido_mymall.domain.usecase.my_rewards

import com.blogspot.mido_mymall.domain.repo.RewardRepo
import javax.inject.Inject

class GetRewardsUseCase @Inject constructor(private val rewardRepo: RewardRepo) {

    suspend operator fun invoke() = rewardRepo.getRewards()
}