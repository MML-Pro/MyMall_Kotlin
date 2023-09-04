package com.blogspot.mido_mymall.domain.usecase.delivery

import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import javax.inject.Inject

class GetAvailableQuantitiesUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

//    suspend operator fun invoke(cartItemModelList: ArrayList<CartItemModel>) =
//        deliveryRepo.getAvailableQuantities(cartItemModelList)
}