package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
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