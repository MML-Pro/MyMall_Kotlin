package com.blogspot.mido_mymall.ui.add_address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.usecase.address.AddAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.GetAddressUseCase
import com.blogspot.mido_mymall.domain.usecase.address.UpdateAddressInfoUseCase
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAddressViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val addAddressUseCase: AddAddressUseCase,
    private val updateAddressInfoUseCase: UpdateAddressInfoUseCase
) :
    ViewModel() {

    private var _addAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val addAddressState: Flow<Resource<Boolean>> get() = _addAddressState

    private var _myAddresses = MutableStateFlow<Resource<DocumentSnapshot>>(Resource.Ideal())
    val myAddresses: Flow<Resource<DocumentSnapshot>> get() = _myAddresses

    private var _updateAddressState = MutableStateFlow<Resource<Boolean>>(Resource.Ideal())
    val updateAddressState: Flow<Resource<Boolean>> get() = _updateAddressState


    fun getAddress() {
        viewModelScope.launch {
            getAddressUseCase().collect {
                _myAddresses.emit(it)
            }
        }
    }

    fun addNewAddress(
        addressesModelList: ArrayList<AddressesModel>,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String? = null,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String? = null,
        selectedAddress: Int
    ) {
        viewModelScope.launch {
            addAddressUseCase(
                addressesModelList = addressesModelList,
                city = city,
                localityOrStreet = localityOrStreet,
                flatNumberOrBuildingName = flatNumberOrBuildingName,
                pinCode = pinCode,
                state = state,
                landMark = landMark,
                fullName = fullName,
                mobileNumber = mobileNumber,
                alternateMobileNumber = alternateMobileNumber,
                selectedAddress = selectedAddress
            ).collect {
                _addAddressState.emit(it)
            }
        }
    }

    fun updateAddressInfo(
        position: Long,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?,
    ) {
        viewModelScope.launch {
            updateAddressInfoUseCase(
                position,
                city,
                localityOrStreet,
                flatNumberOrBuildingName,
                pinCode,
                state,
                landMark,
                fullName,
                mobileNumber,
                alternateMobileNumber
            ).collect {
                _updateAddressState.emit(it)
            }
        }
    }

}