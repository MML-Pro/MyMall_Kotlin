package com.example.mymall_kotlin.domain.usecase.address

import com.example.mymall_kotlin.domain.repo.AddressRepo
import javax.inject.Inject

class UpdateAddressInfoUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(
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
    ) = addressRepo.updateAddressInfo(
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
    )
}