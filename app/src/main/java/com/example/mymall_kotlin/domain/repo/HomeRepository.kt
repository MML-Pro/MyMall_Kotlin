package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getCategories() : Flow<Resource<List<DocumentSnapshot>>>

    suspend fun getTopDeals() :  Flow<Resource<List<DocumentSnapshot>>>
}