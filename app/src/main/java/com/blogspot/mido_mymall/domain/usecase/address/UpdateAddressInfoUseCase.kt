package com.blogspot.mido_mymall.domain.usecase.address

import com.blogspot.mido_mymall.domain.repo.AddressRepo
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