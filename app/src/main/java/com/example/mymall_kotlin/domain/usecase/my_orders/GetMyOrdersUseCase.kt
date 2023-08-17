package com.example.mymall_kotlin.domain.usecase.my_orders

import com.example.mymall_kotlin.domain.repo.MyOrdersRepo
import javax.inject.Inject

class GetMyOrdersUseCase @Inject constructor(private val myOrdersRepo: MyOrdersRepo) {

    suspend operator fun invoke() = myOrdersRepo.getOrders()
}