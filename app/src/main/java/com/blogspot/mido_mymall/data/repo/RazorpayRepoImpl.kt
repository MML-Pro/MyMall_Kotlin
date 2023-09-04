package com.blogspot.mido_mymall.data.repo

import com.blogspot.mido_mymall.data.remote.PaymentApiService
import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.OrderResponse
import com.blogspot.mido_mymall.domain.repo.RazorpayRepo
import com.blogspot.mido_mymall.util.Resource
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