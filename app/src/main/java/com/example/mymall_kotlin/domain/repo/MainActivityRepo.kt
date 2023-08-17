package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface MainActivityRepo {

    suspend fun updateUserLastSeen(): Flow<Resource<Boolean>>

    suspend fun getUserLastSeen(): Flow<Resource<DocumentSnapshot>>


    suspend fun updateOrderStatus(
        orderID: String,
        paymentStatus: String,
        orderStatus: String,
    ): Flow<Resource<Boolean>>

    suspend fun getUserInfo():Flow<Resource<DocumentSnapshot>>
}