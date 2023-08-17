package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class SetRatingUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        productId: String,
        starPosition: Long,
        averageRating: Long,
        totalRatings: Long,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ) = productDetailsRepo.setRating(
        productId,
        starPosition,
        averageRating,
        totalRatings,
        myRatingIds,
        myRating
    )
}