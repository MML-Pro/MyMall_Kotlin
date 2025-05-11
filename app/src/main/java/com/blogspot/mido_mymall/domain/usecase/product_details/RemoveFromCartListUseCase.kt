package com.blogspot.mido_mymall.domain.usecase.product_details

import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class RemoveFromCartListUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        cartListIds: ArrayList<String>,
        index: Int
    ) = productDetailsRepo.removeFromCartList(cartListIds, index)
}