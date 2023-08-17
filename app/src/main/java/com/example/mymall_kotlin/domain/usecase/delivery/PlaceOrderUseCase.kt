package com.example.mymall_kotlin.domain.usecase.delivery

import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.repo.DeliveryRepo
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

    suspend operator fun invoke(
        orderID: String,
        cartItemModelList: ArrayList<CartItemModel>,
        address: String,
        fullName: String,
        pinCode: String,
        paymentMethod: String
    ) = deliveryRepo.placeOrderDetails(
        orderID,
        cartItemModelList,
        address,
        fullName,
        pinCode,
        paymentMethod
    )
}