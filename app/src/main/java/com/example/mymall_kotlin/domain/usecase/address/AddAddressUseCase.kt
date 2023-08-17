package com.example.mymall_kotlin.domain.usecase.address

import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.domain.repo.AddressRepo
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(
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
    ) = addressRepo.addAddress(
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
    )
}