package com.example.mymall_kotlin.domain.usecase.my_rewards

import com.example.mymall_kotlin.domain.repo.RewardRepo
import javax.inject.Inject

class GetRewardsUseCase @Inject constructor(private val rewardRepo: RewardRepo) {

    suspend operator fun invoke() = rewardRepo.getRewards()
}