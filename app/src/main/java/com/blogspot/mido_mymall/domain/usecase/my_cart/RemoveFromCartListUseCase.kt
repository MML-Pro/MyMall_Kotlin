package com.blogspot.mido_mymall.domain.usecase.my_cart

import com.blogspot.mido_mymall.domain.repo.MyCartRepo
import javax.inject.Inject

class RemoveFromCartListUseCase @Inject constructor(
    private val myCartRepo: MyCartRepo
) {

    suspend operator fun invoke(
        cartListIds: ArrayList<String>,
        index: Int
    ) = myCartRepo.removeFromCartList(cartListIds,index)
}