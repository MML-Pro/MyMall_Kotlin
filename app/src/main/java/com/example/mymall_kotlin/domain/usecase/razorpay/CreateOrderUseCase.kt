package com.example.mymall_kotlin.domain.usecase.razorpay

import com.example.mymall_kotlin.domain.models.Order
import com.example.mymall_kotlin.domain.repo.RazorpayRepo
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(private val razorpayRepo: RazorpayRepo) {

    suspend operator fun invoke(
        authHeader: String,
        order: Order
    ) = razorpayRepo.createOrder(authHeader, order)
}