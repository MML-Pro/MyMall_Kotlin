package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.domain.models.WishListModel
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface WishListRepo {

    suspend fun getWishListIds() :Flow<Resource<DocumentSnapshot>>

    suspend fun loadWishList(productId:String) : Flow<Resource<DocumentSnapshot>>

    suspend fun removeFromWishList(
        wishListIds: ArrayList<String>,
        index: Int
    ) : Flow<Resource<Boolean>>
}