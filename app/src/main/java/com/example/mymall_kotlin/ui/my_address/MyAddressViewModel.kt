package com.example.mymall_kotlin.ui.my_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.domain.usecase.address.GetAddressUseCase
import com.example.mymall_kotlin.domain.usecase.address.RemoveAddressUseCase
import com.example.mymall_kotlin.domain.usecase.address.UpdateSelectedUseCase
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAddressViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val updateSelectedUseCase: UpdateSelectedUseCase,
    private val removeAddressUseCase: RemoveAddressUseCase
) : ViewModel() {

    private var _myAddresses = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myAddresses: Flow<Resource<DocumentSnapshot>> get() = _myAddresses

    private var _updateSelectedAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateSelectedAddressState: Flow<Resource<Boolean>> get() = _updateSelectedAddressState

    private var _removeAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val removeAddressState: Flow<Resource<Boolean>> get() = _removeAddressState


    fun getAddresses() {
        viewModelScope.launch {
            getAddressUseCase().collect {
                _myAddresses.emit(it)
            }
        }
    }

    fun updateSelectedAddress(selectedAddress:Int,previousAddress:Int){
        viewModelScope.launch {
            updateSelectedUseCase(selectedAddress, previousAddress).collect{
                _updateSelectedAddressState.emit(it)
            }
        }
    }

    fun removeAddress(addressesModelList: ArrayList<AddressesModel>, position: Int) {

        viewModelScope.launch {
            removeAddressUseCase(addressesModelList, position).collect{
                _removeAddressState.emit(it)
            }
        }
    }
}