package com.example.mymall_kotlin.domain.usecase.my_cart

import com.example.mymall_kotlin.domain.repo.MyCartRepo
import javax.inject.Inject

class GetMyCartListIdsUseCase @Inject constructor(private val myCartRepo: MyCartRepo) {

    suspend operator fun invoke() = myCartRepo.getMyCartListIds()
}