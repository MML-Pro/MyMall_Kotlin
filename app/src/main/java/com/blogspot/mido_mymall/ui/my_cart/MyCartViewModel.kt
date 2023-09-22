package com.blogspot.mido_mymall.ui.my_cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.address.GetAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.my_cart.GetMyCartListIdsUseCase
import com.blogspot.mido_mymall.domain.usecase.my_cart.GetMyCartListUseCase
import com.blogspot.mido_mymall.domain.usecase.my_cart.RemoveFromCartListUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCartViewModel @Inject constructor(
    private val getMyCartListIdsUseCase: GetMyCartListIdsUseCase,
    private val getMyCartListUseCase: GetMyCartListUseCase,
    private val removeFromCartListUseCase: RemoveFromCartListUseCase,
    private val getAddressUseCase: GetAddressUseCase
) : ViewModel() {
    private var _myCartListIds = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myCartListIds: Flow<Resource<DocumentSnapshot>> get() = _myCartListIds

//    private var _cartList = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
//    val cartList: Flow<Resource<DocumentSnapshot>> get() = _cartList

    private var _removeCartListState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val removeCartListState: Flow<Resource<Boolean>> get() = _removeCartListState

    private var _myAddresses = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myAddresses: Flow<Resource<DocumentSnapshot>> get() = _myAddresses


    fun getMyCartListIds() {
        viewModelScope.launch {
            getMyCartListIdsUseCase().collect {
                _myCartListIds.emit(it)
            }
        }
    }

    suspend fun loadMyCartList(productId: String) : Resource<DocumentSnapshot>{
//        _cartList.value = Resource.Loading()
//        viewModelScope.launch {
//            _cartList.emit(getMyCartListUseCase(productId))
//
//        }
        return getMyCartListUseCase(productId)
    }

    fun removeFromCartList(
        cartListIds: ArrayList<String>,
        index: Int
    ) {
        viewModelScope.launch {
            removeFromCartListUseCase(cartListIds, index).collect {
                _removeCartListState.emit(it)
            }
        }
    }

    fun getAddresses() {
        viewModelScope.launch {
            getAddressUseCase().collect {
                _myAddresses.emit(it)
            }
        }
    }
}