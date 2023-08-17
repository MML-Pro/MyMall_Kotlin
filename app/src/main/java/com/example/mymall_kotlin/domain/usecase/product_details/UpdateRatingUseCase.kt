package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class UpdateRatingUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(
        productId: String,
        initialRating: Long,
        oldStar: Long,
        newStar: Long,
        starPosition: Long,
        averageRating: Float,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ) = productDetailsRepo.updateRating(
        productId,
        initialRating,
        oldStar,
        newStar,
        starPosition,
        averageRating,
        myRatingIds,
        myRating
    )
}