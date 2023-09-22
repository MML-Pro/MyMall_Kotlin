package com.blogspot.mido_mymall.domain.usecase.my_cart

import com.blogspot.mido_mymall.domain.repo.MyCartRepo
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class GetMyCartListUseCase @Inject constructor(private val myCartRepo: MyCartRepo) {

    suspend operator fun invoke(productId: String): Resource<DocumentSnapshot> =
        myCartRepo.loadMyCartList(productId)
}