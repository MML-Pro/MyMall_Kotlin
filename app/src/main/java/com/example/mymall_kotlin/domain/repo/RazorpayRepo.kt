package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.domain.models.Order
import com.example.mymall_kotlin.domain.models.OrderResponse
import com.example.mymall_kotlin.util.Resource
import kotlinx.coroutines.flow.Flow

interface RazorpayRepo {

    suspend fun createOrder(
        authHeader: String,
        order: Order
    ): Flow<Resource<OrderResponse>>
}