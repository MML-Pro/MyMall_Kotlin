package com.example.mymall_kotlin.domain.usecase.search

import com.example.mymall_kotlin.domain.repo.SearchRepo
import javax.inject.Inject

class SearchForProductsUseCase @Inject constructor(private val searchRepo: SearchRepo) {

    suspend operator fun invoke(searchQuery:String) = searchRepo.searchForProducts(searchQuery)
}