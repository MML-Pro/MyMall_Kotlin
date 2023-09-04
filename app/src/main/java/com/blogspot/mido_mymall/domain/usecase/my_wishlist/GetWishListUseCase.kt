package com.blogspot.mido_mymall.domain.usecase.my_wishlist

import com.blogspot.mido_mymall.domain.repo.WishListRepo
import javax.inject.Inject

class GetWishListUseCase @Inject constructor(
    private val wishListRepo: WishListRepo
) {

    suspend operator fun invoke(productId:String) = wishListRepo.loadWishList(productId)
}