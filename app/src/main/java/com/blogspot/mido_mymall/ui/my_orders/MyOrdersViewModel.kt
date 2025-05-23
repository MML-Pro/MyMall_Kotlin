package com.blogspot.mido_mymall.ui.my_orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.my_orders.GetMyOrdersUseCase
import com.blogspot.mido_mymall.domain.usecase.my_orders.GetRatingsUseCase
import com.blogspot.mido_mymall.util.Resource
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


//    private var _myOrders = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
//    val myOrders: Flow<Resource<QuerySnapshot>> get() = _myOrders

    private var _ratingsIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val ratingsIds: Flow<Resource<DocumentSnapshot>> get() = _ratingsIds

    private var _userOrders = MutableStateFlow<Resource<QuerySnapshot>>(Resource.Ideal())
    val userOrders: Flow<Resource<QuerySnapshot>> get() = _userOrders

//    fun getMyOrders() {
//        _myOrders.value = Resource.Loading()
//        viewModelScope.launch {
//
//            _myOrders.emit(getMyOrdersUseCase())
//
//        }
//    }

    fun getUserOrders(userId: String){
        _userOrders.value = Resource.Loading()
        viewModelScope.launch {
            _userOrders.emit(getMyOrdersUseCase(userId))
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