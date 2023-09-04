package com.blogspot.mido_mymall.ui.my_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.usecase.my_account.GetLastOrderUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAccountViewModel @Inject constructor(private val getLastOrderUseCase: GetLastOrderUseCase) :
    ViewModel() {

    private val _lastOrder = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val lastOrder get() = _lastOrder.asStateFlow()


    fun getLastOrder() {
        viewModelScope.launch {
            _lastOrder.emit(Resource.Loading())
            _lastOrder.emit(getLastOrderUseCase())
        }
    }
}