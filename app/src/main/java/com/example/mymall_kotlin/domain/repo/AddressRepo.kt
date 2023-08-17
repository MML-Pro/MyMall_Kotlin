package com.example.mymall_kotlin.domain.repo

import com.example.mymall_kotlin.domain.models.AddressesModel
import com.example.mymall_kotlin.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface AddressRepo {

    suspend fun getAddress(): Flow<Resource<DocumentSnapshot>>


    suspend fun addAddress(
        addressesModelList: ArrayList<AddressesModel>,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?,
        selectedAddress: Int
    ): Flow<Resource<Boolean>>

    suspend fun updateSelectedAddress(
        selectedAddress: Int,
        previousAddress: Int
    ): Flow<Resource<Boolean>>

    suspend fun updateAddressInfo(
        position: Long,
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String?,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String?
    ): Flow<Resource<Boolean>>

    suspend fun removeAddress(addressesModelList: ArrayList<AddressesModel>, position: Int): Flow<Resource<Boolean>>
}