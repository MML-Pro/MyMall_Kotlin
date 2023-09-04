package com.blogspot.mido_mymall.domain.usecase.my_account

import com.blogspot.mido_mymall.domain.repo.MyOrdersRepo
import javax.inject.Inject

class GetLastOrderUseCase @Inject constructor(private val myOrdersRepo: MyOrdersRepo) {

    suspend operator fun invoke() = myOrdersRepo.getLastOrder()
}