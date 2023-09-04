package com.blogspot.mido_mymall.domain.usecase.product_details

import com.blogspot.mido_mymall.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class SetRatingUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        productId: String,
        starPosition: Long,
        averageRating: String,
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