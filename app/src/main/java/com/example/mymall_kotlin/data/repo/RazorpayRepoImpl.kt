package com.example.mymall_kotlin.data.repo

import com.example.mymall_kotlin.data.remote.PaymentApiService
import com.example.mymall_kotlin.domain.models.Order
import com.example.mymall_kotlin.domain.models.OrderResponse
import com.example.mymall_kotlin.domain.repo.RazorpayRepo
import com.example.mymall_kotlin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RazorpayRepoImpl @Inject constructor(private val paymentApiService: PaymentApiService) :
    RazorpayRepo {


    override suspend fun createOrder(
        authHeader: String,
        order: Order
    ): Flow<Resource<OrderResponse>> = flow {
        emit(Resource.Loading())

        try {
            val response = paymentApiService.authenticationRequest(authHeader, order)

            emit(Resource.Success(response))
//            if(response.isSuccessful){
//                emit(Resource.Success(response.body()))
//            }else {
//                emit(Resource.Error(response.message().toString()))
//            }



        }catch (ex:Exception){
            emit(Resource.Error(ex.message.toString()))
        }

    }

}