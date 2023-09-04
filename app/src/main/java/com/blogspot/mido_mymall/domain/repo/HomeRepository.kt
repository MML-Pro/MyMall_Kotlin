package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getCategories() : Flow<Resource<List<DocumentSnapshot>>>

    suspend fun getTopDeals() :  Flow<Resource<List<DocumentSnapshot>>>
}