package com.blogspot.mido_mymall.data.remote

import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.OrderResponse
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