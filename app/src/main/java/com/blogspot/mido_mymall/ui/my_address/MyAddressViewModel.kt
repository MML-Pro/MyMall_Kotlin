package com.blogspot.mido_mymall.ui.my_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.usecase.address.GetAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.RemoveAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.UpdateSelectedUseCase
import com.blogspot.mido_mymall.util.Resource
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

    fun removeAddress(addressesModelList:List<AddressesModel>, position: Int, selectedCurrentIndex: Int ) {

        viewModelScope.launch {
            removeAddressUseCase(addressesModelList, position, selectedCurrentIndex).collect{
                _removeAddressState.emit(it)
            }
        }
    }


    fun resetUpdateAddressState() {
        _updateSelectedAddressState.value = Resource.Ideal() // أو أي قيمة تمثل الحالة الأولية/الفارغة
    }

}