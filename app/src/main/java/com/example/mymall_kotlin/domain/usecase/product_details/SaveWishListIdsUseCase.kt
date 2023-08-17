package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class SaveWishListIdsUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(productId: String, wishListSize: Int) =
        productDetailsRepo.saveWishListIds(productId, wishListSize)
}