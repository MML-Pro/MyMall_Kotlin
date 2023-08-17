package com.example.mymall_kotlin.domain.usecase.delivery

import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.repo.DeliveryRepo
import javax.inject.Inject

class GetAvailableQuantitiesUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

//    suspend operator fun invoke(cartItemModelList: ArrayList<CartItemModel>) =
//        deliveryRepo.getAvailableQuantities(cartItemModelList)
}