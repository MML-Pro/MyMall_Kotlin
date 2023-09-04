package com.blogspot.mido_mymall.domain.usecase.delivery

import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
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