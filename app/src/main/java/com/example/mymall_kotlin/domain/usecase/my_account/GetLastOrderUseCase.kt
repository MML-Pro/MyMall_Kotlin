package com.example.mymall_kotlin.domain.usecase.my_account

import com.example.mymall_kotlin.domain.repo.MyOrdersRepo
import javax.inject.Inject

class GetLastOrderUseCase @Inject constructor(private val myOrdersRepo: MyOrdersRepo) {

    suspend operator fun invoke() = myOrdersRepo.getLastOrder()
}