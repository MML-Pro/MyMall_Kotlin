package com.example.mymall_kotlin.domain.usecase.my_wishlist

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import com.example.mymall_kotlin.domain.repo.WishListRepo
import javax.inject.Inject

class GetWishListIdsUseCase @Inject constructor(private val wishListRepo: WishListRepo) {

    suspend operator fun invoke() = wishListRepo.getWishListIds()
}