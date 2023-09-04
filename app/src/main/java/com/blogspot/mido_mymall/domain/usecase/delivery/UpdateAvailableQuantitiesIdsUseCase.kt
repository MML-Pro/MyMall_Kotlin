package com.blogspot.mido_mymall.domain.usecase.delivery

import com.blogspot.mido_mymall.domain.repo.DeliveryRepo
import javax.inject.Inject

class UpdateAvailableQuantitiesIdsUseCase @Inject constructor(private val deliveryRepo: DeliveryRepo) {

//    suspend operator fun invoke(productId: String, documentId: String) =
//        deliveryRepo.updateAvailableQuantitiesIds(productId, documentId)
}