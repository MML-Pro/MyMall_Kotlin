package com.blogspot.mido_mymall.domain.usecase.product_details

import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class RemoveFromWishListUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
        index: Int
    ) = productDetailsRepo.removeFromWishList(wishListIds, wishListModelList, index)
}