package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface MyOrdersRepo {

    suspend fun getOrders(): Flow<Resource<QuerySnapshot>>

    suspend fun getRatings(): Flow<Resource<DocumentSnapshot>>

    suspend fun getLastOrder() :Resource<DocumentSnapshot>


}