package com.example.mymall_kotlin.domain.usecase.home

import com.example.mymall_kotlin.domain.repo.HomeRepository
import javax.inject.Inject

class GetTopDealsUseCase @Inject constructor(private val homeRepository: HomeRepository) {

    suspend operator fun invoke()= homeRepository.getTopDeals()
}