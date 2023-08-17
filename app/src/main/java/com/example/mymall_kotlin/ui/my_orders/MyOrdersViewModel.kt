package com.example.mymall_kotlin.ui.my_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.usecase.my_orders.GetMyOrdersUseCase
import com.example.mymall_kotlin.domain.usecase.my_orders.GetRatingsUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOrdersViewModel @Inject constructor(
    private val getMyOrdersUseCase: GetMyOrdersUseCase,
    private val getRatingsUseCase: GetRatingsUseCase
) :
    ViewModel() {


    private var _myOrders = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
    val myOrders: Flow<Resource<QuerySnapshot>> get() = _myOrders

    private var _ratingsIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val ratingsIds: Flow<Resource<DocumentSnapshot>> get() = _ratingsIds


    fun getMyOrders() {
        viewModelScope.launch {
            getMyOrdersUseCase().collect {
                _myOrders.emit(it)
            }
        }
    }

    fun getRatings() {
        viewModelScope.launch {
            getRatingsUseCase().collect {
                _ratingsIds.emit(it)
            }
        }
    }
}