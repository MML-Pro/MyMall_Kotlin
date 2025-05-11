package com.blogspot.mido_mymall.domain.usecase.address

import com.blogspot.mido_mymall.domain.models.AddressesModel
import com.blogspot.mido_mymall.domain.repo.AddressRepo
import javax.inject.Inject

class RemoveAddressUseCase @Inject constructor(private val addressRepo: AddressRepo) {

    suspend operator fun invoke(
        addressesModelList: List<AddressesModel>,
        position: Int,
        selectedCurrentIndex: Int
    ) = addressRepo.removeAddress(addressesModelList, position,selectedCurrentIndex)
}