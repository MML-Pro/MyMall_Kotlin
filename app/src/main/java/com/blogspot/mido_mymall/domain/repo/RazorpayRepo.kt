package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.OrderResponse
import com.blogspot.mido_mymall.util.Resource
import kotlinx.coroutines.flow.Flow

interface RazorpayRepo {

    suspend fun createOrder(
        authHeader: String,
        order: Order
    ): Flow<Resource<OrderResponse>>
}