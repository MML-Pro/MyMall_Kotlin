package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface SearchRepo {

    suspend fun searchForProducts(searchQuery:String) : Flow<Resource<QuerySnapshot>>

}