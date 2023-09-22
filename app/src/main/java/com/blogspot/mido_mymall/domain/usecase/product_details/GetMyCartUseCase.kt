package com.blogspot.mido_mymall.domain.usecase.product_details

import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class GetMyCartUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke() = productDetailsRepo.getMyCartIds()
}