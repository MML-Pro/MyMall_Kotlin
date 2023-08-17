package com.example.mymall_kotlin.domain.usecase.product_details

import com.example.mymall_kotlin.domain.repo.ProductDetailsRepo
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(private val productDetailsRepo: ProductDetailsRepo) {

    suspend operator fun invoke(productID:String) = productDetailsRepo.getProductDetails(productID)
}