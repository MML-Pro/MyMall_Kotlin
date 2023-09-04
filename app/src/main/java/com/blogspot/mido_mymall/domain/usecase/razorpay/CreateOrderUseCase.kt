package com.blogspot.mido_mymall.domain.usecase.razorpay

import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.repo.RazorpayRepo
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(private val razorpayRepo: RazorpayRepo) {

    suspend operator fun invoke(
        authHeader: String,
        order: Order
    ) = razorpayRepo.createOrder(authHeader, order)
}