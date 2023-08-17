package com.example.mymall_kotlin.domain.usecase.delivery

import com.example.mymall_kotlin.domain.repo.DeliveryRepo
import javax.inject.Inject

class UpdateAvailableQuantitiesIdsUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

//    suspend operator fun invoke(productId: String, documentId: String) =
//        deliveryRepo.updateAvailableQuantitiesIds(productId, documentId)
}