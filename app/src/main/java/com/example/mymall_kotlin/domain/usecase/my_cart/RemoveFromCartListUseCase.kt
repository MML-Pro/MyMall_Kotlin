package com.example.mymall_kotlin.domain.usecase.my_cart

import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.domain.repo.MyCartRepo
import javax.inject.Inject

class RemoveFromCartListUseCase @Inject constructor(
    private val myCartRepo: MyCartRepo
) {

    suspend operator fun invoke(
        cartListIds: ArrayList<String>,
        index: Int
    ) = myCartRepo.removeFromCartList(cartListIds,index)
}