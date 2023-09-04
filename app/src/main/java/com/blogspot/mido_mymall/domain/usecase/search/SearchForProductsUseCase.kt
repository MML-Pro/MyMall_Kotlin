package com.blogspot.mido_mymall.domain.usecase.search

import com.blogspot.mido_mymall.domain.repo.SearchRepo
import javax.inject.Inject

class SearchForProductsUseCase @Inject constructor(private val searchRepo: SearchRepo) {

    suspend operator fun invoke(searchQuery:String) = searchRepo.searchForProducts(searchQuery)
}