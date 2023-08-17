package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class SaveCartListIdsUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(productId: String, cartListSize: Int) =
        productDetailsRepo.saveCartListIds(productId,cartListSize)
}