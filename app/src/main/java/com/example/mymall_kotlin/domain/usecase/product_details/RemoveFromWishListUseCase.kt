package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class RemoveFromWishListUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
        index: Int
    ) = productDetailsRepo.removeFromWishList(wishListIds, wishListModelList, index)
}