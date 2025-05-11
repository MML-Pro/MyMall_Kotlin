package com.blogspot.mido_mymall.domain.usecase.address // تأكد من الحزمة

import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.repo.AddressRepo
import com.blogspot.mido_mymall.util.Resource
import kotlinx.coroutines.flow.Flow // استيراد Flow
import javax.inject.Inject

class AddAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    // إزالة addressesModelList و selectedAddress من هنا
    suspend operator fun invoke(
        city: String,
        localityOrStreet: String,
        flatNumberOrBuildingName: String,
        pinCode: String,
        state: String,
        landMark: String? = null,
        fullName: String,
        mobileNumber: String,
        alternateMobileNumber: String? = null
    ): Flow<Resource<Boolean>> = addressRepo.addAddress( // استدعاء Repo بالمعاملات الصحيحة
        city = city,
        localityOrStreet = localityOrStreet,
        flatNumberOrBuildingName = flatNumberOrBuildingName,
        pinCode = pinCode,
        state = state,
        landMark = landMark,
        fullName = fullName,
        mobileNumber = mobileNumber,
        alternateMobileNumber = alternateMobileNumber
    )
}