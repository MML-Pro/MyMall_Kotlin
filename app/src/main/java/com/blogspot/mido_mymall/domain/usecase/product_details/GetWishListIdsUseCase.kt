package com.blogspot.mido_mymall.domain.usecase.product_details

import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class GetWishListIdsUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke() = productDetailsRepo.getWishListIds()
}