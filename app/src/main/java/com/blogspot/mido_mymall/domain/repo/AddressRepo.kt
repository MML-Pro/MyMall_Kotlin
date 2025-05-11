package com.blogspot.mido_mymall.domain.repo

import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

//interface AddressRepo {
//
//    suspend fun getAddress(): Flow<Resource<DocumentSnapshot>>
//
//
//    suspend fun addAddress(
//        addressesModelList: ArrayList<AddressesModel>,
//        city: String,
//        localityOrStreet: String,
//        flatNumberOrBuildingName: String,
//        pinCode: String,
//        state: String,
//        landMark: String?,
//        fullName: String,
//        mobileNumber: String,
//        alternateMobileNumber: String?,
//        selectedAddress: Int
//    ): Flow<Resource<Boolean>>
//
//    suspend fun updateSelectedAddress(
//        selectedAddress: Int,
//        previousAddress: Int
//    ): Flow<Resource<Boolean>>
//
//    suspend fun updateAddressInfo(
//        position: Long,
//        city: String,
//        localityOrStreet: String,
//        flatNumberOrBuildingName: String,
//        pinCode: String,
//        state: String,
//        landMark: String?,
//        fullName: String,
//        mobileNumber: String,
//        alternateMobileNumber: String?
//    ): Flow<Resource<Boolean>>
//
//    suspend fun removeAddress(addressesModelList: ArrayList<AddressesModel>, position: Int): Flow<Resource<Boolean>>
//}


interface AddressRepo {

    // لم تتغير
    suspend fun getAddress(): Flow<Resource<DocumentSnapshot>>

    // --- تعديل هنا ---
    suspend fun addAddress(
        // حذف addressesModelList و selectedAddress
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

    // لم تتغير (تفترض أنها لا تزال تستخدم الفهارس 0-based)
    suspend fun updateSelectedAddress(
        selectedAddress: Int,
        previousAddress: Int
    ): Flow<Resource<Boolean>>

    // لم تتغير (تفترض أنها لا تزال تستخدم position كـ Long)
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
//
//    // --- تعديل هنا (اختياري ولكنه أفضل) ---
    // تغيير ArrayList إلى List
    suspend fun removeAddress(
        addressesModelList: List<AddressesModel>, // تغيير إلى List
        position: Int,
        selectedCurrentIndex: Int
    // تأكد من أن ViewModel يمرر selectedCurrentIndex إذا كان Repo يحتاجه
        /* , selectedCurrentIndex: Int */
    ): Flow<Resource<Boolean>>
}