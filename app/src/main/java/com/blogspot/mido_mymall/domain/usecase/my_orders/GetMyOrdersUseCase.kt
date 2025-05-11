package com.blogspot.mido_mymall.domain.usecase.my_orders

import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import javax.inject.Inject

class GetMyOrdersUseCase @Inject constructor(private val myOrdersRepo: MyOrdersRepo) {

//    suspend operator fun invoke() = myOrdersRepo.getAllOrders()

    suspend operator fun invoke(userId: String) = myOrdersRepo.getUserOrders(userId)
}