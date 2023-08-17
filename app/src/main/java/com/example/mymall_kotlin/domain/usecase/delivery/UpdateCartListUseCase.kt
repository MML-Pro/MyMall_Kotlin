package com.example.mymall_kotlin.domain.usecase.delivery

import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.repo.DeliveryRepo
import javax.inject.Inject

class UpdateCartListUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

    suspend operator fun invoke(
        cartListIds: ArrayList<String>,
        cartItemModelList: ArrayList<CartItemModel>
    ) = deliveryRepo.updateCartList(cartListIds, cartItemModelList)
}