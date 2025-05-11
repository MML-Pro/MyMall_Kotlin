package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MyOrdersRepo {

//    suspend fun getAllOrders(): Resource<QuerySnapshot>

    suspend fun getRatings(): Flow<Resource<DocumentSnapshot>>

//    suspend fun getLastOrder() : Flow<Resource<DocumentSnapshot>>

    suspend fun getUserOrders(userId: String):Resource<QuerySnapshot>


}