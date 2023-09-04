package com.blogspot.mido_mymall.domain.usecase.main_activity

import com.blogspot.mido_mymall.domain.repo.MainActivityRepo
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(private val mainActivityRepo: MainActivityRepo) {

    suspend operator fun invoke(
        orderID: String,
        paymentStatus: String,
        orderStatus: String,
    ) = mainActivityRepo.updateOrderStatus(orderID, paymentStatus, orderStatus)
}