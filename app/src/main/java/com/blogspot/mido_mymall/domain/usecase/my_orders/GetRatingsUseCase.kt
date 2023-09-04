package com.blogspot.mido_mymall.domain.usecase.my_orders

import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import javax.inject.Inject

class GetRatingsUseCase @Inject constructor(private val myOrdersRepo: MyOrdersRepo) {

    suspend operator fun invoke() = myOrdersRepo.getRatings()
}