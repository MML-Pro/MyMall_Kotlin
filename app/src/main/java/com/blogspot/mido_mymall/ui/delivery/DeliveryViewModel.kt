package com.blogspot.mido_mymall.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.CartItemModel
import com.blogspot.mido_mymall.domain.models.Order
import com.blogspot.mido_mymall.domain.models.OrderResponse
import com.blogspot.mido_mymall.domain.usecase.address.GetAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.delivery.GetAvailableQuantitiesUseCase
import com.blogspot.mido_mymall.domain.usecase.delivery.PlaceOrderUseCase
import com.blogspot.mido_mymall.domain.usecase.delivery.UpdateAvailableQuantitiesIdsUseCase
import com.blogspot.mido_mymall.domain.usecase.delivery.UpdateAvailableQuantitiesUseCase
import com.blogspot.mido_mymall.domain.usecase.delivery.UpdateCartListUseCase
import com.blogspot.mido_mymall.domain.usecase.razorpay.CreateOrderUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val updateCartListUseCase: UpdateCartListUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getAvailableQuantitiesUseCase: GetAvailableQuantitiesUseCase,
    private val updateAvailableQuantitiesUseCase: UpdateAvailableQuantitiesUseCase,
    private val updateAvailableQuantitiesIdsUseCase: UpdateAvailableQuantitiesIdsUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase

) :
    ViewModel() {

    private var _myAddress = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myAddress: Flow<Resource<DocumentSnapshot>> get() = _myAddress

    private var _updateCartListAfterRemove = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateCartListAfterRemove: Flow<Resource<Boolean>> get() = _updateCartListAfterRemove

    private var _orderResponse = MutableStateFlow<Resource<OrderResponse>>(Resource.Ideal())
    val orderResponse: Flow<Resource<OrderResponse>> get() = _orderResponse

    private var _placeOrderStatus = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val placeOrderStatus: Flow<Resource<Boolean>> get() = _placeOrderStatus


    fun getAddress() {
        viewModelScope.launch {
            getAddressUseCase().collect {
                _myAddress.emit(it)
            }
        }
    }

    fun updateCartListAfterRemove(
        cartListIds: ArrayList<String>,
        cartItemModelList: ArrayList<CartItemModel>
    ) {
        viewModelScope.launch {
            updateCartListUseCase(cartListIds, cartItemModelList).collect {
                _updateCartListAfterRemove.emit(it)
            }
        }
    }

    fun createOrder(
        authHeader: String,
        order: Order
    ) {
        viewModelScope.launch {
            createOrderUseCase(authHeader, order).collect {
                _orderResponse.emit(it)
            }
        }
    }

    fun placeOrder(
        orderID: String,
        cartItemModelList: ArrayList<CartItemModel>,
        address: String,
        fullName: String,
        pinCode: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            placeOrderUseCase(
                orderID,
                cartItemModelList,
                address,
                fullName,
                pinCode,
                paymentMethod
            ).collect {
                _placeOrderStatus.emit(it)
            }
        }
    }

//    fun getAvailableQuantities(cartItemModelList: ArrayList<CartItemModel>) {
//        viewModelScope.launch {
//            getAvailableQuantitiesUseCase(cartItemModelList).collect {
//                _availableQuantities.emit(it)
//            }
//        }
//    }
//
//    fun updateAvailableQuantities(productId: String, documentId: String, available: Boolean) {
//        viewModelScope.launch {
//            updateAvailableQuantitiesUseCase(productId, documentId, available).collect {
//                _updateAvailableQuantities.emit(it)
//            }
//        }
//    }
//
//    fun updateAvailableQuantitiesIds(productId: String,documentId: String){
//        viewModelScope.launch {
//            updateAvailableQuantitiesIdsUseCase(productId, documentId).collect {
//                _updateAvailableQuantities.emit(it)
//            }
//        }
//    }
}