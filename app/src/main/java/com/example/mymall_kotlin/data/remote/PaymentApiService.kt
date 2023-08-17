package com.example.mymall_kotlin.data.remote

import com.example.mymall_kotlin.domain.models.Order
import com.example.mymall_kotlin.domain.models.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymentApiService {


    @POST("orders")
    suspend fun authenticationRequest(
        @Header("Authorization") authHeader: String,
        @Body order: Order
    ): OrderResponse


}