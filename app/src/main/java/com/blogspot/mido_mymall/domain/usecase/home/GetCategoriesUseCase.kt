package com.blogspot.mido_mymall.domain.usecase.home

import com.blogspot.mido_mymall.domain.repo.HomeRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val homeRepository: HomeRepository) {

    suspend operator fun invoke() = homeRepository.getCategories()
}