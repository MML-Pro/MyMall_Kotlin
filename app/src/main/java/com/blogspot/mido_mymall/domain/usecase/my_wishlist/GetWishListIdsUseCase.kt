package com.blogspot.mido_mymall.domain.usecase.my_wishlist

import com.blogspot.mido_mymall.domain.repo.WishListRepo
import javax.inject.Inject

class GetWishListIdsUseCase @Inject constructor(private val wishListRepo: WishListRepo) {

    suspend operator fun invoke() = wishListRepo.getWishListIds()
}