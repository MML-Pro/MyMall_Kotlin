package com.blogspot.mido_mymall.domain.usecase.delivery

import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import javax.inject.Inject

class UpdateCartListUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

    suspend operator fun invoke(
        cartListIds: ArrayList<String>,
        cartItemModelList: ArrayList<CartItemModel>
    ) = deliveryRepo.updateCartList(cartListIds, cartItemModelList)
}