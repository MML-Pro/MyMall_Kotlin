package com.blogspot.mido_mymall.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.main_activity.GetUserInfoUseCase
import com.blogspot.mido_mymall.domain.usecase.main_activity.UpdateLastSeenUseCase
import com.blogspot.mido_mymall.domain.usecase.main_activity.UpdateOrderStatusUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val updateLastSeenUseCase: UpdateLastSeenUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private var _lastSeenUpdateState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val lastSeenUpdateState: Flow<Resource<Boolean>> get() = _lastSeenUpdateState

    private var _paymentState = MutableStateFlow(false)
    val paymentState: Flow<Boolean> get() = _paymentState

    private var _orderId = MutableStateFlow("")
    val orderId: Flow<String> get() = _orderId

    private var _updateOrderStatus = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateOrderStatus: Flow<Resource<Boolean>> get() = _updateOrderStatus

    private var _userInfo = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val userInfo: Flow<Resource<DocumentSnapshot>> get() = _userInfo

    fun updateLastSeen() {
        viewModelScope.launch {
            updateLastSeenUseCase().collect {
                _lastSeenUpdateState.emit(it)
            }
        }
    }

    fun updatePaymentState(paymentState: Boolean) {
        _paymentState.value = paymentState
    }

    fun getOrderId(orderId: String) {
        _orderId.value = orderId
    }

    fun updateOrderStatus(
        orderID: String,
        paymentStatus: String,
        orderStatus: String,
    ) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderID, paymentStatus, orderStatus).collect {
                 _updateOrderStatus.emit(it)
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch {
            getUserInfoUseCase().collect{
                _userInfo.emit(it)
            }
        }
    }


}