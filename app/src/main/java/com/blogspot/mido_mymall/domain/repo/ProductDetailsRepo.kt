package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.domain.models.WishListModel
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface ProductDetailsRepo {

    suspend fun getProductDetails(productID: String): Flow<Resource<DocumentSnapshot>>

    suspend fun getWishListIds(): Flow<Resource<DocumentSnapshot>>

    suspend fun saveWishListIds(productId: String, wishListIdsSize: Int): Flow<Resource<Boolean>>

    suspend fun removeFromWishList(
        wishListIds: ArrayList<String>,
        wishListModelList: ArrayList<WishListModel>,
        index: Int
    ): Flow<Resource<Boolean>>


    suspend fun getRatings(): Flow<Resource<DocumentSnapshot>>

    suspend fun setRating(
        productId: String,
        starPosition: Long,
        averageRating: String,
        totalRatings: Long,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ): Flow<Resource<Boolean>>

    suspend fun getMyCartIds(): Resource<DocumentSnapshot>

    suspend fun saveCartListIds(productId: String, carListIdsSize: Int): Resource<Boolean>

    suspend fun updateRating(
        productId: String,
        initialRating: Long,
        oldStar: Long,
        newStar: Long,
        starPosition: Long,
        averageRating: Float,
        myRatingIds: ArrayList<String>,
        myRating: ArrayList<Long>
    ): Flow<Resource<Boolean>>

    suspend fun removeFromCartList(
        cartListIds: ArrayList<String>,
        index: Int
    ): Flow<Resource<Boolean>>


}