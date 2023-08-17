package com.example.mymall_kotlin.domain.usecase.my_wishlist

import com.example.mymall_kotlin.domain.repo.WishListRepo
import javax.inject.Inject

class GetWishListUseCase @Inject constructor(
    private val wishListRepo: WishListRepo
) {

    suspend operator fun invoke(productId:String) = wishListRepo.loadWishList(productId)
}