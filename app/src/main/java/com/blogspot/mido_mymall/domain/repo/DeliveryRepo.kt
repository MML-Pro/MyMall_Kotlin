package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.util.Resource
import kotlinx.coroutines.flow.Flow

interface DeliveryRepo {

    suspend fun updateCartList(
        cartListIds: ArrayList<String>,
        cartItemModelList: ArrayList<CartItemModel>
    ): Flow<Resource<Boolean>>


    suspend fun placeOrderDetails(
        orderID: String,
        cartItemModelList: ArrayList<CartItemModel>,
        address: String,
        fullName: String,
        pinCode: String,
        paymentMethod: String
    ): Flow<Resource<Boolean>>




//    suspend fun getAvailableQuantities(cartItemModelList: ArrayList<CartItemModel>): Flow<Resource<List<DocumentSnapshot>>>
//
//
//    suspend fun updateAvailableQuantities(
//        productId: String, documentId: String,
//        available: Boolean
//    ): Flow<Resource<Boolean>>
//
//    suspend fun updateAvailableQuantitiesIds(
//        productId: String, documentId: String
//    ): Flow<Resource<Boolean>>
}