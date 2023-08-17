package com.example.mymall_kotlin.domain.usecase.my_wishlist

import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import com.example.mymall_kotlin.domain.repo.WishListRepo
import javax.inject.Inject

class RemoveFromWishListUseCase @Inject constructor(private val wishListRepo: WishListRepo) {

    suspend operator fun invoke(
        wishListIds: ArrayList<String>,
        index: Int
    ) = wishListRepo.removeFromWishList(wishListIds, index)
}