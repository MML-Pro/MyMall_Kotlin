package com.blogspot.mido_mymall.domain.usecase.my_cart

import com.blogspot.mido_mymall.domain.repo.MyCartRepo
import javax.inject.Inject

class GetMyCartListIdsUseCase @Inject constructor(private val myCartRepo: MyCartRepo) {

    suspend operator fun invoke() = myCartRepo.getMyCartListIds()
}