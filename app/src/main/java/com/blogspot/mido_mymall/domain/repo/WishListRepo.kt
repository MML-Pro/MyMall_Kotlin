package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.util.Resource
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