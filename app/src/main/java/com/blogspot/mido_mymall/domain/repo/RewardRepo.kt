package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

interface RewardRepo {

    suspend fun getRewards(): Flow<Resource<QuerySnapshot>>


}