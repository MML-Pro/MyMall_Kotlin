package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.domain.models.CartItemModel
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface MyCartRepo {

    suspend fun getMyCartListIds() : Flow<Resource<DocumentSnapshot>>

    suspend fun loadMyCartList(productId:String) : Flow<Resource<DocumentSnapshot>>

    suspend fun removeFromCartList(
        cartListIds: ArrayList<String>,
        index: Int
    ): Flow<Resource<Boolean>>

    suspend fun loadAddress() : Flow<Resource<DocumentSnapshot>>
}